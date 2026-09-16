from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
JAVA = ROOT / "src/main/java/com/szypxj/tlsurvivalelements"
bridge = JAVA / "compat/TdmcAttributeDefinitions.java"
assert bridge.exists(), "missing TDMC attribute-definition bridge"
src = bridge.read_text(encoding="utf-8")
for token in (
    '"tl_survival_elements:food"',
    '"tl_survival_elements:water"',
    '"tl_survival_elements:stamina"',
    '"stat.tl_survival_elements.food"',
    '"stat.tl_survival_elements.water"',
    '"stat.tl_survival_elements.stamina"',
    '"ADDITION"',
    '"showPlayer"',
    '"showMob"',
    'StandardCopyOption.ATOMIC_MOVE',
):
    assert token in src, f"missing bridge contract: {token}"
assert 'root.resolve("tl_domesticate_more_creatures").resolve("attributes.json")' in src
assert 'root.resolve("tl_biological_attribute_panel").resolve("attributes.json")' in src
assert 'water", "stat.tl_survival_elements.water", "minecraft:potion", "tl_survival_elements:water", false' in src
assert 'food", "stat.tl_survival_elements.food", "minecraft:cooked_beef", "tl_survival_elements:food", true' in src
assert 'stamina", "stat.tl_survival_elements.stamina", "minecraft:sugar", "tl_survival_elements:stamina", true' in src
assert "if (reloadSucceeded) return;" in src, "TDMC attribute reload should stop only after a successful reload"
assert "reloadSucceeded = true;" in src, "TDMC attribute reload success must be remembered"
assert "boolean changed = merge(target);\n            tryReloadTdmcAttributes();" in src, "unchanged definitions must still retry TDMC reload at server start"

api = (JAVA / "service/SurvivalAttributeApi.java").read_text(encoding="utf-8")
assert "SurvivalData.of" not in api, "attribute-point API must not initialize resource data"
assert "ModAttributes.foodPoints" in api
assert "ModAttributes.waterPoints" in api
assert "ModAttributes.staminaPoints" in api

events = (JAVA / "event/SurvivalEvents.java").read_text(encoding="utf-8")
main = (JAVA / "TlSurvivalElements.java").read_text(encoding="utf-8")
assert "ServerStartedEvent" in events, "attribute definitions must merge after TDMC server initialization"
assert "onServerStarted" in events
assert "TdmcAttributeDefinitions.ensureInstalled" in events
assert "TdmcAttributeDefinitions.ensureInstalled" not in main, "mod construction must not create TDMC attributes.json before TDMC defaults"
print("SURVIVAL_TDMC_ATTRIBUTE_BRIDGE_OK")
