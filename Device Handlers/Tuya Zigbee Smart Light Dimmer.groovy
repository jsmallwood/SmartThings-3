/**
 *  Copyright 2021 Grzegorz Zalewski
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  READ BEFORE USE
 *  This is totally alpha version work only off and on commands level switch works with wrong values
 *  TODO
 *  * level change command with correct parser
 *  * Configuration
 *
 * NOTES
 * Profile id:0104
 * OutClusters:0019 000A
 * InClusters 0000 0004 0005 EF00
 *
 * profileId:0104,
 * clusterId:EF00,
 * sourceEndpoint:01,
 * destinationEndpoint:01,
 * options:0000,
 * messageType:00,
 * dni:75F9,
 * isClusterSpecific:true, isManufacturerSpecific:false,
 * manufacturerId:0000, command:02, direction:01, data:[E0, 4B, 03, 02, 00, 04, 00, 00, 00, 0A], clusterInt:61184, commandInt:2]
 *
 * RAW DESC
 * 01 0104 0051 01 04 0000 0004 0005 EF00 02 0019 000A
 * Fingerprint
 *
 * Profile id:0104
 * OutClusters:0019 000A
 * InClusters 0000 0004 0005 EF00
 */


/**
 *ON
 *  [raw:0104 EF00 01 01 0000 00 75F9 01 00 0000 01 01 00010101000101,
 *  profileId:0104, clusterId:EF00, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00,
 *  dni:75F9, isClusterSpecific:true, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01,
 *  data:[00, 01, 01, 01, 00, 01, 01], clusterInt:61184, commandInt:1]
 *
 *OFF
 *   [raw:0104 EF00 01 01 0000 00 75F9 01 00 0000 01 01 00010101000100,
 *   profileId:0104, clusterId:EF00, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00,
 *   dni:75F9, isClusterSpecific:true, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01,
 *   data:[00, 01, 01, 01, 00, 01, 00], clusterInt:61184, commandInt:1]
 *
 *Dimmer
 *  [raw:0104 EF00 01 01 0000 00 75F9 01 00 0000 01 01 00010202000400000334,
 *  profileId:0104, clusterId:EF00, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00,
 *  dni:75F9, isClusterSpecific:true, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01,
 *  data:[00, 01, 02, 02, 00, 04, 00, 00, 03, 34], clusterInt:61184, commandInt:1]
 *
 *  [raw:0104 EF00 01 01 0000 00 75F9 01 00 0000 01 01 000102020004000001CC,
 *  profileId:0104, clusterId:EF00, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00,
 *  dni:75F9, isClusterSpecific:true, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01,
 *  data:[00, 01, 02, 02, 00, 04, 00, 00, 01, CC], clusterInt:61184, commandInt:1]
 *
 *  [raw:0104 EF00 01 01 0000 00 75F9 01 00 0000 01 01 00010202000400000064,
 *  profileId:0104, clusterId:EF00, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00,
 *  dni:75F9, isClusterSpecific:true, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01,
 *  data:[00, 01, 02, 02, 00, 04, 00, 00, 00, 64], clusterInt:61184, commandInt:1]
 * */


// on         0104 EF00 01 01 0000 00 3A82 01 00 0000 01 01 00010101000101
// on         0104 EF00 01 01 0000 00 3A82 01 00 0000 01 01 00010101000101
// lvl        0104 EF00 01 01 0000 00 3A82 01 00 0000 01 01 00010202000400000226
// lvl        0104 EF00 01 01 0000 00 3A82 01 00 0000 01 01 00010202000400000172
// off        0104 EF00 01 01 0000 00 3A82 01 00 0000 01 01 00010101000100
// off        0104 EF00 01 01 0000 00 3A82 01 00 0000 01 01 0001020200040000000A
// off        0104 EF00 01 01 0000 00 3A82 01 00 0000 01 01 00010101000100

metadata {
    definition (name: "Zinsoft Tuya ZigBee Dimmer", namespace: "zinsoft", author: "Zinsoft") {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Switch"
        capability "Switch Level"

        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B04, FC0F", outClusters: "0019", manufacturer: "OSRAM", model: "LIGHTIFY A19 ON/OFF/DIM", deviceJoinName: "OSRAM LIGHTIFY LED Smart Connected Light"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, FF00", outClusters: "0019", manufacturer: "MRVL", model: "MZ100", deviceJoinName: "Wemo Bulb"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05", outClusters: "0019", manufacturer: "OSRAM SYLVANIA", model: "iQBR30", deviceJoinName: "Sylvania Ultra iQ"
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"switch level.setLevel"
            }
        }
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        main "switch"
        details(["switch", "refresh"])
    }
}

private getCLUSTER_TUYA() { 0xEF00 }
private getSETDATA() { 0x00 }

// tuya DP type
private getDP_TYPE_BOOL() { "01" }
private getDP_TYPE_VALUE() { "02" }
private getDP_TYPE_ENUM() { "04" }

def devlog(dp, dp_type, fncmd){
    log.debug "Cluster $CLUSTER_TUYA"
    log.debug "Command $SETDATA"
    log.debug "Data ${PACKET_ID + dp +dp_type + zigbee.convertToHexString(fncmd.length()/2, 4) + fncmd }"
}

private sendTuyaCommand(dp, dp_type, fncmd) {
    zigbee.command(CLUSTER_TUYA, SETDATA, PACKET_ID + dp + dp_type + zigbee.convertToHexString(fncmd.length()/2, 4) + fncmd )
}
private getPACKET_ID() {
    state.packetID = ((state.packetID ?: 0) + 1 ) % 65536
    zigbee.convertToHexString(state.packetID, 4)
}

// Parse incoming device messages to generate events
def parse(String description) {
    if (description?.endsWith("00010101000101")) {
        return sendEvent(name: "switch", value: "on")
    }
    if (description?.endsWith("00010101000100")) {
        return sendEvent(name: "switch", value: "off")
    }

    def dhex = description.substring(description.length()-3,description.length())
    def lvl = zigbee.convertHexToInt(dhex)/10
    log.debug "lelvel is $lvl"
    return sendEvent(name: "level", value: lvl)
}


def off() {
    sendTuyaCommand("01", DP_TYPE_ENUM, zigbee.convertToHexString(0))

}

def on() {
    sendTuyaCommand("01", DP_TYPE_ENUM, zigbee.convertToHexString(1))
}

def setLevel(value) {
    log.debug "set level: $value"
    sendTuyaCommand("02", DP_TYPE_VALUE, zigbee.convertToHexString(Math.ceil(value)*10, 2))
}

def refresh() {
    return zigbee.readAttribute(0x0006, 0x0000) +
            zigbee.readAttribute(0x0008, 0x0000) +
            zigbee.configureReporting(0x0006, 0x0000, 0x10, 0, 600, null) +
            zigbee.configureReporting(0x0008, 0x0000, 0x20, 1, 3600, 0x01)
}

def configure() {
    return zigbee.configureReporting(0x0006, 0x0000, 0x10, 0, 600, null) +
            zigbee.configureReporting(0x0008, 0x0000, 0x20, 1, 3600, 0x01) +
            zigbee.readAttribute(0x0006, 0x0000) +
            zigbee.readAttribute(0x0008, 0x0000)
}