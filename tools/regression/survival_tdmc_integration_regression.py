from pathlib import Path
import json

ROOT = Path(__file__).resolve().parents[2]
JAVA = ROOT / 'src/main/java/com/szypxj/tlsurvivalelements'
RES = ROOT / 'src/main/resources'

attrs = (JAVA / 'registry/ModAttributes.java').read_text(encoding='utf-8')
data = (JAVA / 'data/SurvivalData.java').read_text(encoding='utf-8')
api = (JAVA / 'service/SurvivalAttributeApi.java').read_text(encoding='utf-8')
taming = (JAVA / 'compat/TdmcTamingRules.java').read_text(encoding='utf-8')
events = (JAVA / 'event/SurvivalEvents.java').read_text(encoding='utf-8')
math = (JAVA / 'service/SurvivalMath.java').read_text(encoding='utf-8')

assert 'getBaseValue()' in math
assert 'baseMaxHealth(entity) / 5.0D' in math
assert 'setFoodPoints' not in api and 'setWaterPoints' not in api and 'setStaminaPoints' not in api, 'addon must not expose a second point-writing authority'
assert 'public void foodPoints(' not in data and 'public void waterPoints(' not in data and 'public void staminaPoints(' not in data, 'SurvivalData must observe TDMC/Forge stat points, not write them'
assert 'EntityType.PLAYER' in attrs, 'water must be player-only'
assert 'event.add(type, FOOD.get())' in attrs and 'event.add(type, STAMINA.get())' in attrs
assert 'LivingHealEvent' not in events, 'global LivingHealEvent cancellation would also block potion/mod healing'
assert 'native_foods' in taming and 'extra_foods' in taming, 'TDMC taming parser must understand real rule fields'
assert 'RULE_OBJECT' in taming and 'ITEM' in taming, 'TDMC taming parser must parse rule objects and their food entries'

for locale in ('zh_cn', 'en_us'):
    obj = json.loads((RES / f'assets/tl_survival_elements/lang/{locale}.json').read_text(encoding='utf-8'))
    for key in ('stat.tl_survival_elements.food', 'stat.tl_survival_elements.water', 'stat.tl_survival_elements.stamina'):
        assert key in obj

print('SURVIVAL_TDMC_INTEGRATION_OK')
