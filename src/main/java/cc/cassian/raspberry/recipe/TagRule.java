package cc.cassian.raspberry.recipe;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public class TagRule {
    public enum Action { REMOVE_ALL_TAGS, REMOVE_FROM_TAG, CLEAR_TAG }

    private final Action action;
    private final List<ResourceLocation> items;
    private final List<ResourceLocation> tags;

    public TagRule(Action action, List<ResourceLocation> items, List<ResourceLocation> tags) {
        this.action = action;
        this.items = items;
        this.tags = tags;
    }

    public Action getAction() { return action; }
    public List<ResourceLocation> getItems() { return items; }
    public List<ResourceLocation> getTags() { return tags; }
}