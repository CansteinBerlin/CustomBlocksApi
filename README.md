# Custom Blocks Api

*This api works for minecraft versions 1.19.4, 1.20, 1.20.1*

An API for easily creating custom blocks that is heavily inspired by the Fabric API.
For information on how to use this API, see the GitHub wiki.

## Features

*

## Commands

| Command                 | Description                                                                     | Permission                  |
|-------------------------|---------------------------------------------------------------------------------|-----------------------------|
| /listCustomBlocks       | Displays a Gui listing all Custom Blocks. Use left click to get a custom block. | `customblocks.commands.gui` |
| /getCustomBlock <block> | Get the custom block provided.                                                  | `customblocks.commands.get` |

## Screenshots / Examples

<details><summary> Grape Block </summary>
    <img align="middle" src="https://github.com/CansteinBerlin/CustomBlocksApi/blob/master/images/grapes.png" height="200">
    <blockquote><details><summary> Code </summary>

~~~java
public class GrapesBlock extends CustomBlock {

    public static BooleanProperty BERRIES;

    static {
        BERRIES = Properties.BERRIES;
    }

    public GrapesBlock(BlockSettings settings) {
        super(settings, 4, new ItemBuilder(Material.EMERALD).setCustomModelData(4).setDisplayName("§r§6" + settings.getName()).build());
        setDefaultState(getDefaultState().with(BERRIES, false));
    }

    @Override
    public void appendProperties(PropertyListBuilder propertyListBuilder) {
        propertyListBuilder.add(BERRIES);
    }

    @Override
    public CustomBlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(BERRIES, true);
    }

    @Override
    public ActionResult onUse(CustomBlockState state, World world, Location location, Player player, EquipmentSlot hand) {
        if (state.get(BERRIES) == false) {
            return ActionResult.SUCCESS;
        }
        state.with(BERRIES, false);
        state.update();

        //DropItem
        ItemStack stack = new ItemStack(Material.SWEET_BERRIES);
        stack.setAmount(blockRandom.nextInt(2) + 1);
        world.dropItem(location.add(settings.getWidth() / 2, 0, settings.getWidth() / 2), stack);

        //Regrow
        new BukkitRunnable() {

            @Override
            public void run() {
                state.with(BERRIES, true);
                state.update();
            }
        }.runTaskLater(CustomBlocksApiPlugin.getInstance(), blockRandom.nextInt(60) + 200);

        return ActionResult.SUCCESS;
    }

    @Override
    public void applyInitialModelTransformations(ItemDisplay display) {
        display.setRotation(blockRandom.nextInt(360), 0);
    }

    @Override
    public CMDLookupTable createCMDLookupTable(CMDLookupTableBuilder tableBuilder) {
        return tableBuilder.with(BERRIES, false).hasCustomModelData(5).addElement()
                .with(BERRIES, true).hasCustomModelData(4).addElement().build();
    }
}
~~~

</details></blockquote></details>

<details><summary> Animated Computer Block </summary>
    <img align="middle" src="https://github.com/CansteinBerlin/CustomBlocksApi/blob/master/images/computer.png" height="200">
    <blockquote><details><summary> Code </summary>

~~~java
public class AnimatedComputerBlock extends SimpleAnimatedBlock {

    private static BooleanProperty ENABLED;

    static {
        ENABLED = Properties.ENABLED;
    }

    public AnimatedComputerBlock(BlockSettings settings) {
        super(settings, new ItemBuilder(Material.DIAMOND).setCustomModelData(6).setDisplayName("§r§6" + settings.getName()).build(), 10, 7, 6);
        setDefaultState(getDefaultState().with(ENABLED, false));
    }

    @Override
    public void appendProperties(PropertyListBuilder propertyListBuilder) {
        propertyListBuilder.add(ENABLED);
    }

    @Override
    public void onNeighborUpdate(CustomBlockState state, World world, Location location, CustomBlock block, Location fromPos) {
        state.with(ENABLED, location.getBlock().getBlockPower() > 0);
        state.update();
    }

    @Override
    protected boolean shouldPlayFrames(TickState state) {
        return state.getCustomBlockState().get(ENABLED);
    }
}
~~~

</details></blockquote></details>

## Maven Dependency

To add this project as a dependency to your pom.xml, you must first add the repository:

```xml

<repositories>
    <repository>
        <id>hasenzahn-customblocksapi</id>
        <url>https://dl.cloudsmith.io/public/hasenzahn/customblocksapi/maven/</url>
        <releases>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </releases>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

After that you need to add the dependency:

```xml

<dependency>
    <groupId>de.canstein_berlin</groupId>
    <artifactId>CustomBlocksApi</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

## Gradle Dependency

To add this API as a dependency for your Gradle project, make sure that the `dependencies` section in your build.gradle
looks like this
looks like this

```groovy
dependencies {
    implementation 'de.canstein_berlin:CustomBlocksApi:1.0.0'
    // ...
}
```

This project is hosted on Cloudsmith, so make sure your repositories section looks like this

```groovy
repositories {
    maven {
        url "https://dl.cloudsmith.io/public/hasenzahn/customblocksapi/maven/"
    }
    //...
}
```