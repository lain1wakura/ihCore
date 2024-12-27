package org.imperial_hell.qbrp.Resources.Data

import org.imperial_hell.qbrp.Resources.ContentUnits.ContentUnit

abstract class RawData(@Transient val unit: Class<*> = ContentUnit::class.java)