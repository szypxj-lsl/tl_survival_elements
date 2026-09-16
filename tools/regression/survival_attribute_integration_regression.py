from pathlib import Path
import json

ROOT = Path(__file__).resolve().parents[2]
JAVA = ROOT / 'src/main/java/com/szypxj/tlsurvivalelements'
RES = ROOT / 'src/main/resources'

math = (JAVA / 'service/SurvivalMath.java').read_text(encoding='utf-8')
data = (JAVA / 'data/SurvivalData.java').read_text(encoding='utf-8')
attrs = (JAVA / 'registry/ModAttributes.java').read_text(encoding='utf-8')

assert 'getBaseValue()' in math, 'creature food/stamina must be based on MAX_HEALTH base value'
assert 'entity.getMaxHealth() / 5.0D' not in math, 'final max health must not define creature resource bases'
assert 'attribute > 0 ? attribute' not in data, 'zero attribute points must stay zero after TDMC reset'
assert 'FOOD_POINTS' not in data and 'WATER_POINTS' not in data and 'STAMINA_POINTS' not in data, 'point allocation must not have a second NBT authority'
assert 'setSyncable(true)' in attrs, 'addon attributes must synchronize for TDMC/client panels'
assert 'new RangedAttribute("attribute.tl_survival_elements.food"' in attrs
assert 'new RangedAttribute("attribute.tl_survival_elements.water"' in attrs
assert 'new RangedAttribute("attribute.tl_survival_elements.stamina"' in attrs

for locale in ('zh_cn', 'en_us'):
    path = RES / f'assets/tl_survival_elements/lang/{locale}.json'
    obj = json.loads(path.read_text(encoding='utf-8'))
    for key in ('attribute.tl_survival_elements.food', 'attribute.tl_survival_elements.water', 'attribute.tl_survival_elements.stamina',
                'stat.tl_survival_elements.food', 'stat.tl_survival_elements.water', 'stat.tl_survival_elements.stamina'):
        assert key in obj, f'{locale}: missing {key}'
    raw = path.read_text(encoding='utf-8')
    assert '§7' not in raw and '§8' not in raw

print('SURVIVAL_ATTRIBUTE_INTEGRATION_OK')
