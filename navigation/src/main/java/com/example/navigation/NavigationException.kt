package com.example.navigation

class UnknownDestinationException(destination: String) : java.lang.Exception("the given destination $destination doesn't exist")

class MissingExtraException(destination: String, missingExtraName: String) :
    Exception("the given destination $destination require the extra $missingExtraName")

class WrongTypeExtraException(destination: String, wrongTypeExtraName: String, expectedType: String) :
    Exception("The extra $wrongTypeExtraName for the given destination $destination should be a $expectedType")

class NotFragmentHostException(destination: String, currentClass: Class<Any>) :
    Exception("Impossible to route $destination to class ${currentClass.simpleName} must implement the interface ${FragmentHost::class.java.simpleName}")

fun missingExtra(destination: String, missingExtraName: String): Nothing = throw MissingExtraException(destination, missingExtraName)

fun wrongTypeExtra(destination: String, wrongTypeExtraName: String, expectedType: String): Nothing =
    throw WrongTypeExtraException(destination, wrongTypeExtraName, expectedType)

inline fun noFragmentHost(destination: String, currentClass: Class<Any>): Nothing =
    throw NotFragmentHostException(destination, currentClass)