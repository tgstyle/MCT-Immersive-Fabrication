package mctmods.immersivefabrication.data.generators.recipes;

public enum RecipeShape {
    STANDARD(false, false),
    BARE(false, true),
    KEEP_CATEGORY(true, false);

    private final boolean keepCategory;
    private final boolean dropCount;

    RecipeShape(boolean keepCategory, boolean dropCount) {
        this.keepCategory = keepCategory;
        this.dropCount = dropCount;
    }

    public boolean keepCategory() { return keepCategory; }

    public boolean dropCount() { return dropCount; }
}
