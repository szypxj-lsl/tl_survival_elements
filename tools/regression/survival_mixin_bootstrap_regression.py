from pathlib import Path
import json

ROOT = Path(__file__).resolve().parents[2]
build = (ROOT / 'build.gradle').read_text(encoding='utf-8')
mix_path = ROOT / 'src/main/resources/tl_survival_elements.mixins.json'
mix = json.loads(mix_path.read_text(encoding='utf-8'))

assert "org.spongepowered:mixingradle" in build, 'MixinGradle classpath missing'
assert "apply plugin: 'org.spongepowered.mixin'" in build, 'MixinGradle plugin not applied'
assert "add sourceSets.main, 'tl_survival_elements.refmap.json'" in build, 'refmap sourceSet registration missing'
assert "config 'tl_survival_elements.mixins.json'" in build, 'mixin config not registered in build'
assert mix.get('refmap') == 'tl_survival_elements.refmap.json', 'mixin json refmap mismatch'
assert 'FoodDataMixin' in mix.get('mixins', []), 'FoodDataMixin missing from common mixins'
print('SURVIVAL_MIXIN_BOOTSTRAP_OK')
