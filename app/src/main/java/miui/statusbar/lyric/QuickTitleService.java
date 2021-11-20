package miui.statusbar.lyric;

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

@TargetApi(Build.VERSION_CODES.N)
public class QuickTitleService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        Tile tile = getQsTile();
        new Config().setLyricService(!new Config().getLyricService());
        tile.setIcon(Icon.createWithResource(this, R.drawable.title_icon));
        tile.setLabel("状态栏歌词");
        tile.setContentDescription("状态栏歌词");
        tile.setState(new Config().getLyricService() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this, R.drawable.title_icon));
        tile.setLabel("状态栏歌词");
        tile.setContentDescription("状态栏歌词");
        tile.setState(new Config().getLyricService() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
