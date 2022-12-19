# Heatwaves

Heatwaves is a temperature API for Minecraft 1.19.3 Quilt.

## Features

- Get temperature of a block.
- Block temperature sources: provides source temperature for blocks.
- Block temperature keepers: keep a part of temperature wave in a block.

## Usage

### Get temperature of a block

Use `BlockTemperature.getTemperature` to get the temperature of a block.

```java
BlockTemperature.getTemperature(world, pos, true);
```
