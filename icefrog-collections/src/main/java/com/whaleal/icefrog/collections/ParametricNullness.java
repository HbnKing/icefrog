/*
 * Copyright (C) 2021 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.annotation.meta.When.UNKNOWN;

/**
 * Marks a "top-level" type-variable usage as (a) a Kotlin platform type when the type argument is
 * non-nullable and (b) nullable when the type argument is nullable. This is the closest we can get
 * to "non-nullable when non-nullable; nullable when nullable" (like the Android <a
 * href="https://android.googlesource.com/platform/libcore/+/master/luni/src/main/java/libcore/util/NullFromTypeParam.java">{@code
 * NullFromTypeParam}</a>). We use this to "undo" {@link ElementTypesAreNonnullByDefault}.
 */

@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
@TypeQualifierNickname
@Nonnull(when = UNKNOWN)
@interface ParametricNullness {}
