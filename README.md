# FasterChests

Chests in Minecraft have no geometry in the chunk mesh. Every visible chest is drawn by a
`BlockEntityRenderer` that re-extracts its render state every frame, including a neighbour-block
query to decide whether it is half of a double chest. In a storage room this is the single largest
avoidable cost in the frame.

FasterChests gives chests real block models so they bake into the chunk mesh like any other block,
and stops the block entity renderer from drawing them a second time.

**The animated lid is not drawn.** This is **INTENTIONAL** and an artifact of the changed rendering

## Measured

In a chest-dense storage room: **~200 FPS -> ~2300 FPS**, i.e. roughly 4.5 ms of frame time
returned per frame. The gain scales with the number of chests in view, so an ordinary overworld
scene will see far less than this.

## Configuration

`config/fasterchests.properties`

```properties
staticChests=true
```

Set `staticChests=false` to restore the vanilla animated chest lid. The mod then leaves rendering
entirely to Minecraft, so there is no double-drawing.

Supported: 26.1.2 and 26.2, on Fabric and NeoForge. Client-side only. No dependencies.

## Credit

The block model, blockstate and atlas resources are taken from
[FastChest](https://github.com/FakeDomi/FastChest) by FakeDomi. FasterChests ports
the approach to Minecraft 26.x.

## License

GPL-3.0-or-later. See `LICENSE`.

The blockstate, model and atlas resources originate from FastChest and remain under their
original MIT terms; see `NOTICE`.
