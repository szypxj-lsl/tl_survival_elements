# 生存要素 / Survival Elements

`tl_survival_elements` 是 Minecraft Forge 1.20.1 / Forge 47.4.10 的 TDMC 附属模组。

## 依赖

- Minecraft 1.20.1
- Forge 47.4.10
- Java 17
- `tl_domesticate_more_creatures` 1.0.0+

## 核心机制

- 玩家基础最大生命为 100。
- 玩家基础食物、水分、耐力均为 100。
- 非玩家生物的基础食物与基础耐力 = `MAX_HEALTH` 基础值 ÷ 5。
- 生物基础资源只读取 `AttributeInstance#getBaseValue()`，不会被装备、Buff 或最终最大生命的临时变化带着波动。
- 食物、水分、耐力均注册为 Forge Attribute，并通过 TDMC 的动态属性定义兼容层参与加点。
- 每投入 1 个 TDMC 属性点，资源最大值增加该资源基础值的 10%。
- 玩家显示食物、水分、耐力；生物显示食物、耐力，不注册水分属性。
- 耐力标准固定消耗 = `sqrt(基础耐力) / 3`，并受最小/最大物种消耗配置限制。加耐力点只扩大容量，不提高标准消耗。
- 食物或水分低于 20% 时阻止自然回血；TDMC 自身等级回血也受同一门槛限制。
- 玩家水分低于 20% 获得口渴状态，耐力恢复倍率默认降为 50%。
- 原版水瓶与普通饮用药水默认恢复水分，也支持按物品 ID 或物品标签配置第三方饮品恢复量。
- TDMC 驯服食物默认要求目标当前食物低于 90% 才能继续喂食；阈值由服务端配置控制。
- 已由 TDMC 管理的宠物可以用普通可食用物补充食物；满食物时不会额外消耗普通食物。
- 骑乘 TDMC 管理坐骑时：Shift 加速、Ctrl 下降/下潜、F 下马、E 打开坐骑 TDMC 面板。
- HUD 使用深蓝色半透明资源条替代原版生命、护甲、食物、氧气与坐骑生命显示。

## TDMC 属性面板兼容

启动时本模组会合并以下三项到 TDMC 动态属性定义文件，而不是覆盖原有定义：

- `food` -> `tl_survival_elements:food`
- `water` -> `tl_survival_elements:water`
- `stamina` -> `tl_survival_elements:stamina`

优先配置路径：

`config/tl_domesticate_more_creatures/attributes.json`

同时兼容旧路径：

`config/tl_biological_attribute_panel/attributes.json`

TDMC 仍然是属性点、可分配点、野生随机点和重置逻辑的唯一权威；Survival Elements 不保存第二套属性点。

## 配置

Forge COMMON 配置由游戏生成。详细字段与示例见 `docs/CONFIGURATION.md`。

## 源码验证

`tools/regression/` 中提供源码级回归。该工程的交付检查不以这些脚本替代 Forge 编译；本次按项目约定不执行 Gradle 编译。
