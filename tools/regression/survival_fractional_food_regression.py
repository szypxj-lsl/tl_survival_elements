from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
JAVA = ROOT / 'src/main/java/com/szypxj/tlsurvivalelements'

data = (JAVA / 'data/SurvivalData.java').read_text(encoding='utf-8')
service = (JAVA / 'service/SurvivalService.java').read_text(encoding='utf-8')

assert 'FOOD_DRAIN_BUFFER' in data, 'player fractional food drain needs a persistent accumulator'
assert 'public void drainFood(double amount)' in data, 'resource layer must expose exact fractional food consumption'
assert 'data.drainFood(applied);' in service, 'healing drain must use fractional-safe food consumption'
assert 'data.drainFood(data.maxFood() * SurvivalConfig.FOOD_DRAIN_PER_MINUTE' in service, 'natural drain must use fractional-safe food consumption'
assert 'data.food(data.food() - applied);' not in service
print('SURVIVAL_FRACTIONAL_FOOD_OK')
