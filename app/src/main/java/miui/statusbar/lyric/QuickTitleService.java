package miui.statusbar.lyric;

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import miui.statusbar.lyric.utils.ActivityUtils;

@TargetApi(Build.VERSION_CODES.N)
public class QuickTitleService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        Tile tile = getQsTile();
        Config config = ActivityUtils.getConfig(getApplicationContext());
        config.setLyricService(!config.getLyricService());
        set(tile, config);
    }

    private void set(Tile tile, Config config) {
        tile.setIcon(Icon.createWithResource(this, R.drawable.title_icon));
        tile.setLabel(getString(R.string.QuickTitle));
        tile.setContentDescription(getString(R.string.QuickTitle));
        tile.setState(config.getLyricService() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        Config config = ActivityUtils.getConfig(getApplicationContext());
        Tile tile = getQsTile();
        set(tile, config);
    }
}
