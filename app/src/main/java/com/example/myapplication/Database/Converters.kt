package com.example.assi2.Database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    class ListIntConverter {
        @TypeConverter
        fun fromListInt(value: List<Int>?): String? {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun toListInt(value: String?): List<Int>? {
            val listType = object : TypeToken<List<Int>>() {}.type
            return Gson().fromJson(value, listType)
        }
    }

    class ListStringConverter {

        @TypeConverter
        fun fromString(value: String): List<String> {
            return value.split(",")
        }

        @TypeConverter
        fun toString(list: List<String>): String {
            return list.joinToString(",")
        }
    }

    class ListDoubleConverter {

        @TypeConverter
        fun fromListDouble(value: List<Double>?): String? {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun toListDouble(value: String?): List<Double>? {
            val listType = object : TypeToken<List<Double>>() {}.type
            return Gson().fromJson(value, listType)
        }
    }
    class ListFloatConverter {
        @TypeConverter
        fun fromListFloat(value: List<Float>?): String? {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun toListFloat(value: String?): List<Float>? {
            val listType = object : TypeToken<List<Float>>() {}.type
            return Gson().fromJson(value, listType)
        }
    }

}