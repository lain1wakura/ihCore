package org.imperial_hell.common.Proxy

import org.imperial_hell.common.Blocks.BlockData.Overlays.Overlay

data class ProxyBlockData(val overlays: List<Overlay> = listOf<Overlay>()) {
}