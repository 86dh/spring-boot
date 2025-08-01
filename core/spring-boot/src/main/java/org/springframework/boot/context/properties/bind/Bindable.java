/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.context.properties.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.core.ResolvableType;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Source that can be bound by a {@link Binder}.
 *
 * @param <T> the source type
 * @author Phillip Webb
 * @author Madhura Bhave
 * @since 2.0.0
 * @see Bindable#of(Class)
 * @see Bindable#of(ResolvableType)
 */
public final class Bindable<T> {

	private static final Annotation[] NO_ANNOTATIONS = {};

	private static final EnumSet<BindRestriction> NO_BIND_RESTRICTIONS = EnumSet.noneOf(BindRestriction.class);

	private final ResolvableType type;

	private final ResolvableType boxedType;

	private final @Nullable Supplier<T> value;

	private final Annotation[] annotations;

	private final EnumSet<BindRestriction> bindRestrictions;

	private final @Nullable BindMethod bindMethod;

	private Bindable(ResolvableType type, ResolvableType boxedType, @Nullable Supplier<T> value,
			Annotation[] annotations, EnumSet<BindRestriction> bindRestrictions, @Nullable BindMethod bindMethod) {
		this.type = type;
		this.boxedType = boxedType;
		this.value = value;
		this.annotations = annotations;
		this.bindRestrictions = bindRestrictions;
		this.bindMethod = bindMethod;
	}

	/**
	 * Return the type of the item to bind.
	 * @return the type being bound
	 */
	public ResolvableType getType() {
		return this.type;
	}

	/**
	 * Return the boxed type of the item to bind.
	 * @return the boxed type for the item being bound
	 */
	public ResolvableType getBoxedType() {
		return this.boxedType;
	}

	/**
	 * Return a supplier that provides the object value or {@code null}.
	 * @return the value or {@code null}
	 */
	public @Nullable Supplier<T> getValue() {
		return this.value;
	}

	/**
	 * Return any associated annotations that could affect binding.
	 * @return the associated annotations
	 */
	public Annotation[] getAnnotations() {
		return this.annotations;
	}

	/**
	 * Return a single associated annotations that could affect binding.
	 * @param <A> the annotation type
	 * @param type annotation type
	 * @return the associated annotation or {@code null}
	 */
	@SuppressWarnings("unchecked")
	public <A extends Annotation> @Nullable A getAnnotation(Class<A> type) {
		for (Annotation annotation : this.annotations) {
			if (type.isInstance(annotation)) {
				return (A) annotation;
			}
		}
		return null;
	}

	/**
	 * Returns {@code true} if the specified bind restriction has been added.
	 * @param bindRestriction the bind restriction to check
	 * @return if the bind restriction has been added
	 * @since 2.5.0
	 */
	public boolean hasBindRestriction(BindRestriction bindRestriction) {
		return this.bindRestrictions.contains(bindRestriction);
	}

	/**
	 * Returns the {@link BindMethod method} to be used to bind this bindable, or
	 * {@code null} if no specific binding method is required.
	 * @return the bind method or {@code null}
	 * @since 3.0.8
	 */
	public @Nullable BindMethod getBindMethod() {
		return this.bindMethod;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Bindable<?> other = (Bindable<?>) obj;
		boolean result = true;
		result = result && nullSafeEquals(this.type.resolve(), other.type.resolve());
		result = result && nullSafeEquals(this.annotations, other.annotations);
		result = result && nullSafeEquals(this.bindRestrictions, other.bindRestrictions);
		result = result && nullSafeEquals(this.bindMethod, other.bindMethod);
		return result;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHash(this.type, this.annotations, this.bindRestrictions, this.bindMethod);
	}

	@Override
	public String toString() {
		ToStringCreator creator = new ToStringCreator(this);
		creator.append("type", this.type);
		creator.append("value", (this.value != null) ? "provided" : "none");
		creator.append("annotations", this.annotations);
		creator.append("bindMethod", this.bindMethod);
		return creator.toString();
	}

	private boolean nullSafeEquals(@Nullable Object o1, @Nullable Object o2) {
		return ObjectUtils.nullSafeEquals(o1, o2);
	}

	/**
	 * Create an updated {@link Bindable} instance with the specified annotations.
	 * @param annotations the annotations
	 * @return an updated {@link Bindable}
	 */
	public Bindable<T> withAnnotations(Annotation @Nullable ... annotations) {
		return new Bindable<>(this.type, this.boxedType, this.value,
				(annotations != null) ? annotations : NO_ANNOTATIONS, NO_BIND_RESTRICTIONS, this.bindMethod);
	}

	/**
	 * Create an updated {@link Bindable} instance with an existing value. Implies that
	 * Java Bean binding will be used.
	 * @param existingValue the existing value
	 * @return an updated {@link Bindable}
	 */
	public Bindable<T> withExistingValue(@Nullable T existingValue) {
		Assert.isTrue(existingValue == null || this.type.isArray() || boxedTypeIsInstanceOf(existingValue),
				() -> "'existingValue' must be an instance of " + this.type);
		Assert.state(this.bindMethod != BindMethod.VALUE_OBJECT,
				() -> "An existing value cannot be provided when binding as a value object");
		Supplier<T> value = (existingValue != null) ? () -> existingValue : null;
		return new Bindable<>(this.type, this.boxedType, value, this.annotations, this.bindRestrictions,
				BindMethod.JAVA_BEAN);
	}

	private boolean boxedTypeIsInstanceOf(T existingValue) {
		Class<?> resolved = this.boxedType.resolve();
		return resolved != null && resolved.isInstance(existingValue);
	}

	/**
	 * Create an updated {@link Bindable} instance with a value supplier.
	 * @param suppliedValue the supplier for the value
	 * @return an updated {@link Bindable}
	 */
	public Bindable<T> withSuppliedValue(@Nullable Supplier<T> suppliedValue) {
		return new Bindable<>(this.type, this.boxedType, suppliedValue, this.annotations, this.bindRestrictions,
				this.bindMethod);
	}

	/**
	 * Create an updated {@link Bindable} instance with additional bind restrictions.
	 * @param additionalRestrictions any additional restrictions to apply
	 * @return an updated {@link Bindable}
	 * @since 2.5.0
	 */
	public Bindable<T> withBindRestrictions(BindRestriction... additionalRestrictions) {
		EnumSet<BindRestriction> bindRestrictions = EnumSet.copyOf(this.bindRestrictions);
		bindRestrictions.addAll(Arrays.asList(additionalRestrictions));
		return new Bindable<>(this.type, this.boxedType, this.value, this.annotations, bindRestrictions,
				this.bindMethod);
	}

	/**
	 * Create an updated {@link Bindable} instance with a specific bind method. To use
	 * {@link BindMethod#VALUE_OBJECT value object binding}, the current instance must not
	 * have an existing or supplied value.
	 * @param bindMethod the method to use to bind the bindable
	 * @return an updated {@link Bindable}
	 * @since 3.0.8
	 */
	public Bindable<T> withBindMethod(@Nullable BindMethod bindMethod) {
		Assert.state(bindMethod != BindMethod.VALUE_OBJECT || this.value == null,
				() -> "Value object binding cannot be used with an existing or supplied value");
		return new Bindable<>(this.type, this.boxedType, this.value, this.annotations, this.bindRestrictions,
				bindMethod);
	}

	/**
	 * Create a new {@link Bindable} of the type of the specified instance with an
	 * existing value equal to the instance.
	 * @param <T> the source type
	 * @param instance the instance (must not be {@code null})
	 * @return a {@link Bindable} instance
	 * @see #of(ResolvableType)
	 * @see #withExistingValue(Object)
	 */
	@SuppressWarnings("unchecked")
	public static <T> Bindable<T> ofInstance(T instance) {
		Assert.notNull(instance, "'instance' must not be null");
		Class<T> type = (Class<T>) instance.getClass();
		return of(type).withExistingValue(instance);
	}

	/**
	 * Create a new {@link Bindable} of the specified type.
	 * @param <T> the source type
	 * @param type the type (must not be {@code null})
	 * @return a {@link Bindable} instance
	 * @see #of(ResolvableType)
	 */
	public static <T> Bindable<T> of(Class<T> type) {
		Assert.notNull(type, "'type' must not be null");
		return of(ResolvableType.forClass(type));
	}

	/**
	 * Create a new {@link Bindable} {@link List} of the specified element type.
	 * @param <E> the element type
	 * @param elementType the list element type
	 * @return a {@link Bindable} instance
	 */
	public static <E> Bindable<List<E>> listOf(Class<E> elementType) {
		return of(ResolvableType.forClassWithGenerics(List.class, elementType));
	}

	/**
	 * Create a new {@link Bindable} {@link Set} of the specified element type.
	 * @param <E> the element type
	 * @param elementType the set element type
	 * @return a {@link Bindable} instance
	 */
	public static <E> Bindable<Set<E>> setOf(Class<E> elementType) {
		return of(ResolvableType.forClassWithGenerics(Set.class, elementType));
	}

	/**
	 * Create a new {@link Bindable} {@link Map} of the specified key and value type.
	 * @param <K> the key type
	 * @param <V> the value type
	 * @param keyType the map key type
	 * @param valueType the map value type
	 * @return a {@link Bindable} instance
	 */
	public static <K, V> Bindable<Map<K, V>> mapOf(Class<K> keyType, Class<V> valueType) {
		return of(ResolvableType.forClassWithGenerics(Map.class, keyType, valueType));
	}

	/**
	 * Create a new {@link Bindable} of the specified type.
	 * @param <T> the source type
	 * @param type the type (must not be {@code null})
	 * @return a {@link Bindable} instance
	 * @see #of(Class)
	 */
	public static <T> Bindable<T> of(ResolvableType type) {
		Assert.notNull(type, "'type' must not be null");
		ResolvableType boxedType = box(type);
		return new Bindable<>(type, boxedType, null, NO_ANNOTATIONS, NO_BIND_RESTRICTIONS, null);
	}

	private static ResolvableType box(ResolvableType type) {
		Class<?> resolved = type.resolve();
		if (resolved != null && resolved.isPrimitive()) {
			Object array = Array.newInstance(resolved, 1);
			Class<?> wrapperType = Array.get(array, 0).getClass();
			return ResolvableType.forClass(wrapperType);
		}
		if (resolved != null && resolved.isArray()) {
			return ResolvableType.forArrayComponent(box(type.getComponentType()));
		}
		return type;
	}

	/**
	 * Restrictions that can be applied when binding values.
	 *
	 * @since 2.5.0
	 */
	public enum BindRestriction {

		/**
		 * Do not bind direct {@link ConfigurationProperty} matches.
		 */
		NO_DIRECT_PROPERTY

	}

}
