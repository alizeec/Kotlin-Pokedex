package com.example.navigation

/**
 * NO_DUPLICATE_ON_TOP: A - B (launch B) -> A - B
 * BACK_ON_LAST_INSTANCE_AND_CLEAR_BACKSTACK: A - B - C (launch B) -> A - B
 * CLEAR_WHOLE_BACKSTACK: A - B (launch C) -> C
 */
enum class LaunchScreenFlags {
    NO_DUPLICATE_ON_TOP,
    BACK_ON_LAST_INSTANCE_AND_CLEAR_BACKSTACK,
    CLEAR_WHOLE_BACKSTACK
}