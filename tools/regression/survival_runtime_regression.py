from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
JAVA = ROOT / 'src/main/java/com/szypxj/tlsurvivalelements'

data = (JAVA / 'data/SurvivalData.java').read_text(encoding='utf-8')
events = (JAVA / 'event/SurvivalEvents.java').read_text(encoding='utf-8')
service = (JAVA / 'service/SurvivalService.java').read_text(encoding='utf-8')

assert 'public void syncCapacity()' in data
assert 'data.syncCapacity();' in events, 'server tick must observe TDMC/external stat changes'
assert 'data.water() <= 0.0D' in service, 'zero hydration must disable player boost/sprint'
assert 'SurvivalConfig.PLAYER_STAMINA_COST_MULTIPLIER' in service
assert 'SurvivalConfig.PET_STAMINA_COST_MULTIPLIER' in service
assert 'SurvivalConfig.PLAYER_WATER_DRAIN_MULTIPLIER' in service
assert 'SurvivalConfig.PLAYER_FOOD_DRAIN_MULTIPLIER' in service
assert 'SurvivalConfig.PET_FOOD_DRAIN_MULTIPLIER' in service
assert 'Math.sqrt(baseStamina(entity)) / 3.0D' in (JAVA / 'service/SurvivalMath.java').read_text(encoding='utf-8')
print('SURVIVAL_RUNTIME_OK')
