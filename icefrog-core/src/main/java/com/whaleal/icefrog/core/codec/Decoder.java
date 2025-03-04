/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whaleal.icefrog.core.codec;

/**
 * Decoders are used for reading source types from DB or others and converting them into Java objects.
 *
 * @param <T> the type to decode into, the return type of the {@link #decode(Reader, DecoderContext)} method.
 */
public interface Decoder<T> {
	/**
	 * Decodes a value from the given reader into an instance of the type parameter {@code T}.
	 *
	 * @param reader         the BSON reader
	 * @param decoderContext the decoder context
	 * @return an instance of the type parameter {@code T}.
	 */
	T decode(Reader reader, DecoderContext decoderContext);
}

