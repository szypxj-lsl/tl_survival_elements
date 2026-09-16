from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
JAVA = ROOT / 'src/main/java/com/szypxj/tlsurvivalelements'

packet = (JAVA / 'network/packet/S2CSurvivalSyncPacket.java').read_text(encoding='utf-8')
network = (JAVA / 'network/SurvivalNetwork.java').read_text(encoding='utf-8')
client = (JAVA / 'client/RideKeyHandler.java').read_text(encoding='utf-8')
c2s = (JAVA / 'network/packet/C2SRideControlPacket.java').read_text(encoding='utf-8')
runtime = (JAVA / 'service/RideRuntime.java').read_text(encoding='utf-8')

assert 'boolean mountManaged' in packet, 'sync packet must carry mountManaged'
assert 'TdmcCompat.isManagedPet(mount)' in network, 'server snapshot must resolve TDMC managed mount'
assert 'snapshot.mountManaged()' in client, 'client interception must be scoped to managed mounts'
assert 'TdmcCompat.isManagedPet(mount)' in c2s or 'TdmcCompat.isManagedPet(mount)' in runtime, 'server ride controls must reject unmanaged mounts'
assert 'player.stopRiding()' in c2s, 'F dismount path must remain server-authoritative'
print('SURVIVAL_RIDE_SCOPE_OK')
