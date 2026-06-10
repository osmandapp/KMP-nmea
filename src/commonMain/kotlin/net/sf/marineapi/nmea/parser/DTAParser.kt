/* 
 * DTAParser.java
 * Copyright (C) 2019 Kimmo Tuukkanen
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
package net.sf.marineapi.nmea.parser

import net.sf.marineapi.nmea.sentence.DTASentence
import net.sf.marineapi.nmea.sentence.SentenceId
import net.sf.marineapi.nmea.sentence.TalkerId

import kotlinx.datetime.LocalDateTime


/**
 * DTA sentence parser.
 *
 * @author Bob Schwarz
 * @see [marine-api fork](https://github.com/LoadBalanced/marine-api)
 */
internal open class DTAParser : SentenceParser, DTASentence {
    private val offset = if (getFieldCount() >= 8) 0 else -1

    /**
     * Creates a new instance of DTAParser with 8 data fields.
     *
     * @param talker DTA talkerId
     */
    constructor(talker: TalkerId?) : super(talker, SentenceId.DTA, 8)

    /**
     * Creates a new instance of DTAParser.
     *
     * @param nmea DTA sentence String
     */
    constructor(nmea: String) : super(nmea, SentenceId.DTA)

    /**
     * Creates a new instance of DTAParser.
     *
     * @param nmea DTA sentence String
     */
    protected constructor(nmea: String, type: SentenceId) : super(nmea, type)

    /**
     * Creates a new instance of DTAParser with specified type and data fields.
     *
     * @param talker DTA talkerId
     * @param type SentenceId enum
     * @param size number of data fields in DTASentence (not counting header and checksum).
     */
    protected constructor(talker: TalkerId?, type: SentenceId, size: Int) : super(talker, type, size)

    /**
     * Returns the field index fixed with possible offset.
     */
    private fun getFieldIndex(field: Int): Int {
        return offset + field
    }

    override fun getChannelNumber(): Int {
        return if (offset == -1) 1 else getIntValue(CHANNEL_NUMBER)
    }

    override fun getGasConcentration(): Double {
        return getDoubleValue(getFieldIndex(GAS_CONCENTRATION))
    }

    override fun getConfidenceFactorR2(): Int {
        return getIntValue(getFieldIndex(CONFIDENCE_FACTOR_R2))
    }

    override fun getDistance(): Double {
        return getDoubleValue(getFieldIndex(DISTANCE))
    }

    override fun getLightLevel(): Int {
        return getIntValue(getFieldIndex(LIGHT_LEVEL))
    }

    override fun getDateTime(): LocalDateTime {
        val str = getStringValue(getFieldIndex(DATE_TIME))
        try {
            val parts = str.split(" ")
            val dateParts = parts[0].split("/")
            val timeParts = parts[1].split(":")
            return LocalDateTime(
                dateParts[0].toInt(), dateParts[1].toInt(), dateParts[2].toInt(),
                timeParts[0].toInt(), timeParts[1].toInt(), timeParts[2].toInt()
            )
        } catch (ex: Exception) {
            throw ParseException("Field does not contain date value", ex)
        }
    }

    override fun getSerialNumber(): String {
        return getStringValue(getFieldIndex(SER_NUMBER))
    }

    override fun getStatusCode(): Int {
        return getIntValue(getFieldIndex(STATUS_CODE))
    }

    companion object {
        private const val CHANNEL_NUMBER = 0
        private const val GAS_CONCENTRATION = 1
        private const val CONFIDENCE_FACTOR_R2 = 2
        private const val DISTANCE = 3
        private const val LIGHT_LEVEL = 4
        private const val DATE_TIME = 5
        private const val SER_NUMBER = 6
        private const val STATUS_CODE = 7
    }
}