package com.bkmbigo.movieapp.api.dto

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class PSAripsFeed @JvmOverloads constructor(
    @field:Element(name = "channel")
    var channel: PSAripsChannel = PSAripsChannel()
)

@Root(name = "channel", strict = false)
data class PSAripsChannel @JvmOverloads constructor(
    @field:Element(name="title")
    var title: String = "",
    @field:Element(name="link", required = false)
    var link: String = "",
    @field:Element(name="lastBuildDate")
    var lastBuildDate: String = "",
    @field:ElementList(name = "item", inline = true, required = false)
    var items: List<PSAItem> = ArrayList()
)

@Root(name = "item", strict = false)
data class PSAItem @JvmOverloads constructor(
    @field:Element(name="title")
    var title: String = "",
    @field:Element(name="link")
    var link: String = "",
    @field:Element(name="description")
    var description: String = "",
    @field:ElementList(name = "category", inline = true, required = false)
    var category: List<String> = ArrayList()
)