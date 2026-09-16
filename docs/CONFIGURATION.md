# 生存要素配置说明

Forge COMMON 配置会由 Forge 自动生成。

## 玩家倍率

- `player.staminaCostMultiplier = 1.0`：玩家耐力消耗倍率。
- `player.waterDrainMultiplier = 1.0`：玩家水分消耗倍率。
- `player.foodDrainMultiplier = 1.0`：玩家食物消耗倍率。

## 宠物倍率

- `pet.staminaCostMultiplier = 1.0`：TDMC 管理宠物耐力消耗倍率。
- `pet.foodDrainMultiplier = 1.0`：宠物食物消耗倍率。

## 食物

- `food.naturalDrainPercentPerMinute = 0.01`：每分钟自然消耗最大食物的 1%。
- `food.anesthesiaDrainMultiplier = 5.0`：麻醉/眩晕相关效果存在时的食物消耗倍率。
- `food.healingDrainPercent = 0.10`：一次回血消耗周期总共消耗最大食物的 10%。
- `food.healingDrainTicks = 100`：默认 100 Tick，即 5 秒内完成该次食物消耗。
- `food.creatureFixedRestore = 5.0`：生物每次有效进食固定恢复量。
- `food.creaturePercentRestore = 0.01`：生物每次有效进食额外恢复最大食物的 1%。
- `food.tamingFeedCurrentPercentMaximum = 0.90`：TDMC 驯服喂食要求当前食物低于 90%。

## 水分

- `water.naturalDrainPercentPerMinute = 0.01`：每分钟自然消耗最大水分的 1%。
- `water.activeDrainMultiplier = 2.0`：奔跑/坐骑加速时水分消耗倍率。
- `water.defaultPotionRestore = 30.0`：原版水瓶与普通饮用药水默认恢复值。
- `water.itemRestoreRules`：第三方饮品/食物补水规则。

### 自定义物品补水

格式：

```text
物品ID=恢复值
```

例如：

```text
farmersdelight:apple_cider=35
```

### 按物品标签补水

格式：

```text
#标签ID=恢复值
```

例如：

```text
#forge:drinks=25
```

直接物品规则优先于默认药水恢复逻辑。标签规则按配置顺序匹配。

## 耐力

- `stamina.stillRegenPercentPerSecond = 0.05`：静止并达到 1 秒空闲后每秒恢复 5%。
- `stamina.movingRegenPercentPerSecond = 0.01`：普通移动且未加速时每秒恢复 1%。
- `stamina.thirstRegenMultiplier = 0.5`：口渴时玩家耐力恢复倍率。
- `stamina.boostSpeedMultiplier = 1.5`：坐骑加速倍率。
- `stamina.groundCostMultiplier = 1.0`
- `stamina.swimCostMultiplier = 1.0`
- `stamina.flightCostMultiplier = 1.25`
- `stamina.attackCostMultiplier = 1.0`
- `stamina.minimumSpeciesCost = 0.5`
- `stamina.maximumSpeciesCost = 8.0`
- `stamina.entityCostOverrides`：按实体 ID 覆盖标准耐力消耗。

实体覆盖格式：

```text
实体ID=标准消耗
```

例如：

```text
minecraft:horse=1.5
```

覆盖值仍会乘动作倍率与玩家/宠物耐力消耗倍率，但不会随耐力属性加点增加。

## 低资源门槛

- `lowResourceThreshold = 0.20`：默认 20%。食物低于该比例不能自然/TDMC回血；玩家水分低于该比例同样不能回血并进入口渴状态。

## 麻醉效果列表

`anesthesiaEffectIds` 可追加其他模组麻醉/昏迷效果 ID，用于提高麻醉期间食物消耗。
