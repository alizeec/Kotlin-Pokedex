package com.example.navigation

interface IRouteParser {

    fun parse(destination: String, extras: Map<String, Any>?): Route?
}