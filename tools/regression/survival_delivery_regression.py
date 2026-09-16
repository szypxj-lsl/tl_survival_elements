from pathlib import Path
import json
import re

ROOT = Path(__file__).resolve().parents[2]
JAVA = ROOT / 'src/main/java/com/szypxj/tlsurvivalelements'
RES = ROOT / 'src/main/resources'

assert (ROOT / 'README.md').is_file()
assert (ROOT / 'docs/CONFIGURATION.md').is_file()
assert (ROOT / 'build.gradle').is_file()
assert (ROOT / 'settings.gradle').is_file()
assert (ROOT / 'gradle.properties').is_file()

mods = (RES / 'META-INF/mods.toml').read_text(encoding='utf-8')
props = (ROOT / 'gradle.properties').read_text(encoding='utf-8')
assert 'mod_id=tl_survival_elements' in props
assert 'mod_version=1.0.0' in props
assert 'forge_version=47.4.10' in props
assert 'modId="tl_domesticate_more_creatures"' in mods
assert 'versionRange="[1.0.0,)"' in mods

for path in RES.rglob('*.json'):
    json.loads(path.read_text(encoding='utf-8'))

mix = json.loads((RES / 'tl_survival_elements.mixins.json').read_text(encoding='utf-8'))
for name in mix.get('mixins', []):
    assert (JAVA / 'mixin' / f'{name}.java').is_file(), f'missing mixin source: {name}'
for name in mix.get('client', []):
    assert (JAVA / 'mixin' / f'{name}.java').is_file(), f'missing client mixin source: {name}'

zh = json.loads((RES / 'assets/tl_survival_elements/lang/zh_cn.json').read_text(encoding='utf-8'))
en = json.loads((RES / 'assets/tl_survival_elements/lang/en_us.json').read_text(encoding='utf-8'))
assert set(zh) == set(en), 'zh_cn/en_us keys must match exactly'
for raw in ((RES / 'assets/tl_survival_elements/lang/zh_cn.json').read_text(encoding='utf-8'),
            (RES / 'assets/tl_survival_elements/lang/en_us.json').read_text(encoding='utf-8')):
    assert '§7' not in raw and '§8' not in raw

all_java = '\n'.join(p.read_text(encoding='utf-8') for p in JAVA.rglob('*.java'))
for key in re.findall(r'Component\.translatable\("([^"]+)"', all_java):
    assert key in zh and key in en, f'missing language key: {key}'
assert 'Component.literal(' not in all_java, 'player-visible Component.literal found'

math = (JAVA / 'service/SurvivalMath.java').read_text(encoding='utf-8')
assert 'getBaseValue()' in math
assert 'baseMaxHealth(entity) / 5.0D' in math
assert 'Math.sqrt(baseStamina(entity)) / 3.0D' in math

bridge = (JAVA / 'compat/TdmcAttributeDefinitions.java').read_text(encoding='utf-8')
assert 'root.resolve("tl_domesticate_more_creatures").resolve("attributes.json")' in bridge
assert 'tryReloadTdmcAttributes();' in bridge
assert 'reloadSucceeded = true;' in bridge

print('SURVIVAL_DELIVERY_OK')
