/*
 * Copyright (C) 2020 Gunnar Hillert
 *
 * This file is part of Java Marine API.
 * <http://ktuukkan.github.io/marine-api/>
 *
 * Java Marine API is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Java Marine API is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java Marine API. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.marineapi.ublox.parser

import net.sf.marineapi.nmea.sentence.UBXSentence
import net.sf.marineapi.ublox.message.UBXMessage

/**
 * Factory for creating UBX message parsers. Currently the following parsers are supported:
 *
 *
 *  * [UBXMessage00Parser]
 *  * [UBXMessage03Parser]
 *
 *
 * @author Gunnar Hillert
 */
class UBXMessageFactory private constructor() {
    private val parsers: MutableMap<Int, (UBXSentence) -> UBXMessage> = mutableMapOf()

    /**
     * Hidden constructor.
     */
    init {
        parsers[0] = { s -> UBXMessage00Parser(s) }
        parsers[3] = { s -> UBXMessage03Parser(s) }
    }

    /**
     * Creates a new UBX message parser based on given [UBXSentence].
     *
     * @param sentence The UBX sentence to pass to the UBXMessageParser
     * @throws IllegalArgumentException If given message type is not supported.
     * @throws IllegalStateException If message parser cannot be constructed
     * due to illegal state, e.g. invalid or empty message.
     * @return UBXMessage instance
     */
    fun create(sentence: UBXSentence): UBXMessage {
        val parser = UBXMessageParser(sentence)
        val constructor = parsers[parser.getMessageType()]
            ?: throw IllegalArgumentException("no parser for message type ${parser.getMessageType()}")
        return constructor(sentence)
    }

    companion object {
        private var instance: UBXMessageFactory? = null

        /**
         * Returns the factory singleton.
         *
         * @return UBXMessageFactory
         */
        fun getInstance(): UBXMessageFactory? {
            if (instance == null) instance = UBXMessageFactory()
            return instance
        }
    }
}