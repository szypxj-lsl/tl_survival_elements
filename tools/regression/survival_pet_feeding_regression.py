from pathlib import Path
import json

ROOT = Path(__file__).resolve().parents[2]
JAVA = ROOT / 'src/main/java/com/szypxj/tlsurvivalelements'
RES = ROOT / 'src/main/resources/assets/tl_survival_elements/lang'
MIXIN = ROOT / 'src/main/resources/tl_survival_elements.mixins.json'

events = (JAVA / 'event/SurvivalEvents.java').read_text(encoding='utf-8')
service = (JAVA / 'service/SurvivalService.java').read_text(encoding='utf-8')
mixin_path = JAVA / 'mixin/PlayerInteractOnMixin.java'
assert mixin_path.exists(), 'feeding must observe the real Player#interactOn return value'
mixin = mixin_path.read_text(encoding='utf-8')

assert 'PENDING_FEEDS' not in events and 'onTamingFeedApplied' not in events, 'pre-interaction Forge priority cannot be used as an after-interaction hook'
assert 'creatureNeedsFood' in service, 'managed pet feeding needs a full-food guard'
assert '@Mixin(Player.class)' in mixin
assert 'method = "interactOn"' in mixin
assert '@At("HEAD")' in mixin and '@At("RETURN")' in mixin
assert 'cir.getReturnValue().consumesAction()' in mixin, 'accepted feeding must be based on the actual entity interaction result'
assert 'self.getAbilities().instabuild' in mixin, 'creative feeding must not consume the held item'
assert '.shrink(1)' in mixin, 'ordinary edible fallback must consume one item for non-creative players'
assert 'InteractionResult.SUCCESS' in mixin, 'ordinary managed-pet edible fallback must consume the interaction'
assert 'SurvivalService.creatureNeedsFood(living)' in mixin
assert 'managedPet && petFood && !SurvivalService.creatureNeedsFood(target)' in events, 'full pets must be gated before native interaction can consume food'
assert 'SurvivalService.restoreCreatureFood(living)' in mixin
assert 'SurvivalService.canTamingFeed(living)' in mixin, 'post hook must retain the configured taming gate as a safety check'
assert 'TAME_FEED_THRESHOLD.get() * 100.0D' in events, 'taming warning must derive its percent from config'
assert 'Component.translatable("msg.tl_survival_elements.taming_food_too_high", thresholdPercent)' in events

mix = json.loads(MIXIN.read_text(encoding='utf-8'))
assert 'PlayerInteractOnMixin' in mix.get('mixins', [])

zh = json.loads((RES / 'zh_cn.json').read_text(encoding='utf-8'))
en = json.loads((RES / 'en_us.json').read_text(encoding='utf-8'))
key = 'msg.tl_survival_elements.taming_food_too_high'
assert '%s' in zh[key] and '%s' in en[key], 'taming threshold message must not hardcode 90%'
assert '90%' not in zh[key] and '90%' not in en[key]

print('SURVIVAL_PET_FEEDING_OK')
