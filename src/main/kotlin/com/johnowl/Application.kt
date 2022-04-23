package com.johnowl

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.runtime.Micronaut.build
import java.math.BigDecimal

fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.johnowl")
		.start()
}

data class User(val preferences: List<String>, val location: String, val balance: BigDecimal)
data class City(val name: String, val location: String, val characteristics: List<String>, val nearestAirport: String)
data class Flight(val number: String, val from: String, val to: String, val price: BigDecimal, val seatClass: String)

const val BEACH = "beach"
const val HISTORICAL = "historical"
const val NIGHT_LIFE = "night_life"
const val COUNTRY_SIDE = "country_side"
const val WINE = "wine"

const val ECONOMY = "economy"
const val EXECUTIVE = "executive"
const val FIRST_CLASS = "first_class"

@Controller
class TravelController {

	private val users: Set<User> = setOf(
		User(listOf(BEACH), "NETHERLANDS", BigDecimal(100)),
		User(listOf(HISTORICAL), "NETHERLANDS", BigDecimal(300)),
		User(listOf(WINE, HISTORICAL), "NETHERLANDS", BigDecimal(500)),
		User(listOf(BEACH, NIGHT_LIFE), "NETHERLANDS", BigDecimal(1500)),
		User(listOf(WINE, COUNTRY_SIDE), "NETHERLANDS", BigDecimal(300)),
	)

	private val cities = setOf(
		City("Paris", "FRANCE", listOf(HISTORICAL, NIGHT_LIFE), "CDG"),
		City("London", "ENGLAND", listOf(HISTORICAL, NIGHT_LIFE), "LHR"),
		City("Ibiza", "SPAIN", listOf(BEACH, NIGHT_LIFE), "IBZ"),
		City("Mallorca", "SPAIN", listOf(BEACH, NIGHT_LIFE), "PMI"),
	)

	private val flights = setOf(
		Flight("1234", "AMS", "CDG", BigDecimal(80), ECONOMY),
		Flight("1235", "AMS", "LHR", BigDecimal(120), ECONOMY),
		Flight("1236", "AMS", "IBZ", BigDecimal(170), ECONOMY),
		Flight("1237", "AMS", "PMI", BigDecimal(300), ECONOMY),
		Flight("1237", "AMS", "PMI", BigDecimal(500), EXECUTIVE),
		Flight("1237", "AMS", "PMI", BigDecimal(1500), FIRST_CLASS),
	)

	@Get("recommendations")
	fun recommendFlights(
		@QueryValue preferences: List<String>,
		@QueryValue budget: BigDecimal,
		@QueryValue(defaultValue = ECONOMY) seatClass: String
	): List<Flight> {
		val recommendedCities = cities.filter { city ->
			preferences.any { preference ->
				preference.lowercase() in city.characteristics
			}
		}

		return flights.filter { flight ->
			recommendedCities.any { city ->
				city.nearestAirport == flight.to && flight.price <= budget && flight.seatClass.lowercase() == seatClass.lowercase()
			}
		}
	}
}
