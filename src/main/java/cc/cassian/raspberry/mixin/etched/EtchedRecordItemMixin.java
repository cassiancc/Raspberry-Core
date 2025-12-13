package cc.cassian.raspberry.mixin.etched;

import cc.cassian.raspberry.client.music.MusicHandler;
import gg.moonflower.etched.api.record.AlbumCover;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.record.TrackData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import java.net.Proxy;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Pseudo
@Mixin(value = RecordItem.class, priority = 1500)
public class EtchedRecordItemMixin implements PlayableRecord {
    @Override
    public Optional<TrackData[]> getMusic(ItemStack stack) {
        RecordItem disc = (RecordItem) (Object) this;
        MusicHandler.MusicMetadata info = MusicHandler.getDiscInfo(disc);

        TrackData track = new TrackData(
            disc.getSound().getLocation().toString(),
            info.author().getString(),
            info.title()
        );

        return Optional.of(new TrackData[]{track});
    }

    @Override
    public Optional<TrackData> getAlbum(ItemStack stack) {
        return this.getMusic(stack).map(tracks -> tracks[0]);
    }

    @Override
    public int getTrackCount(ItemStack stack) {
        return 1;
    }

    @Override
    public CompletableFuture<AlbumCover> getAlbumCover(ItemStack stack, Proxy proxy, ResourceManager resourceManager) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) return CompletableFuture.completedFuture(AlbumCover.EMPTY);
        
        return CompletableFuture.completedFuture(AlbumCover.of(id));
    }
}