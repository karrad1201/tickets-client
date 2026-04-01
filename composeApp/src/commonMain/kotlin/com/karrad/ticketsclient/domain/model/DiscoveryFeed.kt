package com.karrad.ticketsclient.domain.model

data class DiscoveryFeed(
    val forYou: List<Event>,
    val byCategory: Map<Category, List<Event>>,
    val tomorrow: List<Event>,
    val dayAfterTomorrow: List<Event>
)
