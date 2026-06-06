package com.mirrorx.app

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class QuickSettingsTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile
        if (tile != null) {
            tile.state = Tile.STATE_INACTIVE
            tile.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        @Suppress("DEPRECATION")
        startActivityAndCollapse(intent)
    }
}
