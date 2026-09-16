from pathlib import Path
import json

ROOT = Path(__file__).resolve().parents[2]
JAVA = ROOT / 'src/main/java/com/szypxj/tlsurvivalelements'
RES = ROOT / 'src/main/resources'

bridge = JAVA / 'compat/TdmcHealingBridge.java'
mixin = JAVA / 'mixin/LivingEntityHealMixin.java'
assert bridge.is_file(), 'missing TDMC healing caller bridge'
assert mixin.is_file(), 'missing LivingEntity heal mixin'

bridge_text = bridge.read_text(encoding='utf-8')
mixin_text = mixin.read_text(encoding='utf-8')
service = (JAVA / 'service/SurvivalService.java').read_text(encoding='utf-8')
events = (JAVA / 'event/SurvivalEvents.java').read_text(encoding='utf-8')
mixins = json.loads((RES / 'tl_survival_elements.mixins.json').read_text(encoding='utf-8'))

assert 'com.szypxj.tldomesticatemorecreatures.game.LevelService' in bridge_text
assert 'StackWalker' in bridge_text
assert 'LivingHealEvent' not in events
assert '@Mixin(LivingEntity.class)' in mixin_text
assert 'SurvivalService.canNaturalHeal' in mixin_text
assert 'SurvivalService.beginHealingDrain' in mixin_text
assert 'TdmcHealingBridge.isTdmcLevelHealing()' in mixin_text
assert 'LivingEntityHealMixin' in mixins.get('mixins', [])
assert 'duration <= 20' in service or 'getDuration() <= 20' in service, 'thirst effect should not be re-added every server tick'

print('SURVIVAL_HEALING_BRIDGE_OK')
