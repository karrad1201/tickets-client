package com.karrad.ticketsclient.domain.repository

import com.karrad.ticketsclient.domain.model.City

interface CityRepository {
    /** Returns all known cities. */
    fun getAll(): List<City>

    /**
     * Returns cities whose name or subject name contains [query] (case-insensitive).
     * Empty query returns all cities.
     */
    fun search(query: String): List<City>
}
