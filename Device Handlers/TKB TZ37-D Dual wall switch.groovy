/**
 *  TKB TZ37-D Dual wall switch
 *
 *  Copyright 2019 Grzegorz Zalewski
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
 *
 *	Note: device id's and models for these TKB home devices are a mess: re-used id's and/or no real certification. Just use one of the switch or dimmer versions
 *  and change to the proper DTH needed after including.
 */

// still have a problem with spawning children device to control lights separately
metadata {
    definition (name: "TKB TZ37-D Dual wall switch", namespace: "Zinsoft", author: "Grzegorz Zalewski") {
        capability "Health Check"
        capability "Refresh"
        capability "Sensor"
        capability "Switch"
        capability "Configuration"


        //zw:L type:1001 mfr:0118 prod:0311 model:0203 ver:2.18 zwv:4.05 lib:03 cc:5E,86,72,5A,73,85,59,25,20,27,70,2B,2C,60,7A role:05 ff:8701 ui:8700 epc:2
        fingerprint mfr:"0118", prod:"0311", model:"0203", deviceJoinName: "TKB TZ37-D Dual wall switch"

    }


    simulator {
        // TODO: define status and reply messages here
    }

    preferences {

        input name: "includeGroup1", type: "text",  title: "Include devices in group 1 - tap right button once (Enter Device Network Id's comma separated)"
        input name: "includeGroup2", type: "text",  title: "Include devices in group 2 - left right button twice (Enter Device Network Id's comma separated)"
        input name: "includeGroup3", type: "text",  title: "Include devices in group 3 - tap right button twice (Enter Device Network Id's comma separated)"
        input name: "includeGroup4", type: "text",  title: "Include devices in group 4 - devices follow load (Enter Device Network Id's comma separated)"
        input name : "nightLight", type: "enum", options: ["0" : "LED is ON when the load attached is OFF", "1" : "LED is ON when the load attached is ON"], title: "Night light", description: "The LED behaviour based on the load attached. (default value is ON)"
        input name : "ignoreStartLevelBit", type: "enum", options: ["0" : "Do not ignore start level","1" : "Ignore start level"], title: "Set Ignore when transmitting dim commands", description: "The dimmer either ignore the start level and start dimming from its current level or use the start level (default value is Ignore start level)"
        input name : "invertSwitch", type: "enum", options: ["0": "Top button = On", "1": "Top button = Off"], title: "Invert switch", description: "Switch on/off behaviour (default value is Top button = On)"
        input name : "ledTransmissionIndication", type: "enum", options: ["0": "No flicker","1": "1 second", "2": "entire transmission duration"], title: "LED Transmission indication", discription: "The device can flicker its LED when it is transmitting to any of its 4 groups. (default value is 1 second) "
        input name : "disableTransmittingToGroup4", type:"enum", options: ["0" : "Do not transmit", "1": "Transmit"], title:"Disable transmitting to group 4", description : "Control transmission to devices that are associated into Group 4. (default value is: Do not transmit)"
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
            tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC"
                attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
            }
        }

        standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
        }

        main "switch"
        details(["switch", "refresh"])
    }
}

def installed() {
    def componentLabel
    if (device.displayName.endsWith('1')) {
        componentLabel = "${device.displayName[0..-2]}2"
    } else {

        componentLabel = "$device.displayName 2"
        componentLabel2 = "$device.displayName 3"
    }
    try {
        String dni = "${device.deviceNetworkId}-ep2"
        addChildDevice("Z-Wave Binary Switch Endpoint", dni, device.hub.id,
                [completedSetup: true, label: "${componentLabel}", isComponent: false])
        log.debug "Endpoint 2 (Z-Wave Binary Switch Endpoint) added as $componentLabel"
    } catch (e) {
        log.warn "Failed to add endpoint 2 ($desc) as Z-Wave Binary Switch Endpoint - $e"
    }
    configure()
}
def updated() {
    configure()
}

def configure() {

    //log.debug "Sending Configuration to device"
    sendEvent(name: "checkInterval", value: 30 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
    def cmds = []

    if (nightLight) { cmds << zwave.configurationV1.configurationSet(parameterNumber: 1, size: 1, scaledConfigurationValue: nightLight.toInteger()).format() }
    if (ignoreStartLevelBit) { cmds << zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, scaledConfigurationValue: ignoreStartLevelBit.toInteger()).format() }
    if (invertSwitch) { cmds << zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: invertSwitch.toInteger()).format() }
    if (ledTransmissionIndication) { cmds << zwave.configurationV1.configurationSet(parameterNumber: 4, size: 1, scaledConfigurationValue: ledTransmissionIndication.toInteger()).format() }
    if (disableTransmittingToGroup4) { cmds << zwave.configurationV1.configurationSet(parameterNumber: 5, size: 1, scaledConfigurationValue: disableTransmittingToGroup4.toInteger()).format() }

    cmds << zwave.associationV2.associationRemove(groupingIdentifier: 1, nodeId: []).format()
    if (includeGroup1) {
        def deviceList = includeGroup1.split(',')
        deviceList.each {switchDev ->
            def nodeId = Integer.parseInt(switchDev,16)
            //log.debug "Group 1 included node: ${nodeId}"
            cmds << zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:[nodeId]).format()
        }
    }
    cmds << zwave.associationV2.associationRemove(groupingIdentifier: 2, nodeId: []).format()
    if (includeGroup2) {
        def deviceList = includeGroup2.split(',')
        deviceList.each {switchDev ->
            def nodeId = Integer.parseInt(switchDev,16)
            //log.debug "Group 2 included node: ${nodeId}"
            cmds << zwave.associationV1.associationSet(groupingIdentifier:2, nodeId:[nodeId]).format()
        }
    }
    cmds << zwave.associationV2.associationRemove(groupingIdentifier: 3, nodeId: []).format()
    if (includeGroup3) {
        def deviceList = includeGroup3.split(',')
        deviceList.each {switchDev ->
            def nodeId = Integer.parseInt(switchDev,16)
            //log.debug "Group 3 included node: ${nodeId}"
            cmds << zwave.associationV1.associationSet(groupingIdentifier:3, nodeId:[nodeId]).format()
        }
    }
    cmds << zwave.associationV2.associationRemove(groupingIdentifier: 4, nodeId: []).format()
    if (includeGroup4) {
        def deviceList = includeGroup4.split(',')
        deviceList.each {switchDev ->
            def nodeId = Integer.parseInt(switchDev,16)
            //log.debug "Group 4 included node: ${nodeId}"
            cmds << zwave.associationV1.associationSet(groupingIdentifier:4, nodeId:[nodeId]).format()
        }
    }
    delayBetween(cmds,1500)
}
private secure(physicalgraph.zwave.Command cmd) {
    //log.trace(cmd)
    zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}
/**
 * Mapping of command classes and associated versions used for this DTH
 */
private getCommandClassVersions() {
    [
            0x20: 1,  // Basic
            0x25: 1,  // Switch Binary
            0x30: 1,  // Sensor Binary
            0x31: 2,  // Sensor MultiLevel
            0x32: 3,  // Meter
            0x56: 1,  // Crc16Encap
            0x60: 3,  // Multi-Channel
            0x70: 2,  // Configuration
            0x84: 1,  // WakeUp
            0x98: 1,  // Security
            0x9C: 1   // Sensor Alarm
    ]
}

def parse(String description) {
    def result = null
    def cmd = zwave.parse(description, commandClassVersions)
    if (cmd) {
        result = zwaveEvent(cmd)
    }
    log.debug("'$description' parsed to $result")
    return createEvent(result)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd, endpoint=null) {
    (endpoint == 1) ? [name: "switch", value: cmd.value ? "on" : "off"] : [:]
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd, endpoint=null) {
    (endpoint == 1) ? [name: "switch", value: cmd.value ? "on" : "off"] : [:]
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd, endpoint=null) {
    (endpoint == 1) ? [name: "switch", value: cmd.value ? "on" : "off"] : [:]
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
    def encapsulatedCommand = cmd.encapsulatedCommand(commandClassVersions)
    if (encapsulatedCommand) {
        zwaveEvent(encapsulatedCommand)
    } else {
        log.warn "Unable to extract encapsulated cmd from $cmd"
        createEvent(descriptionText: cmd.toString())
    }
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
    def encapsulatedCommand = cmd.encapsulatedCommand([0x32: 3, 0x25: 1, 0x20: 1])
    if (cmd.sourceEndPoint == 1) {
        zwaveEvent(encapsulatedCommand, 1)
    } else { // sourceEndPoint == 2
        childDevices[0]?.handleZWave(encapsulatedCommand)
        [:]
    }
}

def zwaveEvent(physicalgraph.zwave.commands.crc16encapv1.Crc16Encap cmd) {
    def versions = commandClassVersions
    def version = versions[cmd.commandClass as Integer]
    def ccObj = version ? zwave.commandClass(cmd.commandClass, version) : zwave.commandClass(cmd.commandClass)
    def encapsulatedCommand = ccObj?.command(cmd.command)?.parse(cmd.data)
    if (encapsulatedCommand) {
        zwaveEvent(encapsulatedCommand)
    }
    [:]
}

def zwaveEvent(physicalgraph.zwave.Command cmd, endpoint = null) {
    if (endpoint == null) log.debug("$device.displayName: $cmd")
    else log.debug("$device.displayName: $cmd endpoint: $endpoint")
}

def on() {
    // parent DTH controls endpoint 1
    def endpointNumber = 1
    delayBetween([
            encap(endpointNumber, zwave.switchBinaryV1.switchBinarySet(switchValue: 0xFF)),
            encap(endpointNumber, zwave.switchBinaryV1.switchBinaryGet())
    ])
}

def off() {
    // parent DTH controls endpoint 1
    def endpointNumber = 1
    delayBetween([
            encap(endpointNumber, zwave.switchBinaryV1.switchBinarySet(switchValue: 0x00)),
            encap(endpointNumber, zwave.switchBinaryV1.switchBinaryGet())
    ])
}

/**
 * PING is used by Device-Watch in attempt to reach the Device
 * */
def ping() {
    refresh()
}

def refresh() {
    // parent DTH controls endpoint 1
    [encap(1, zwave.switchBinaryV1.switchBinaryGet()), encap(2, zwave.switchBinaryV1.switchBinaryGet())]
}

// sendCommand is called by endpoint 2 child device handler
def sendCommand(endpointDevice, commands) {
    //There is only 1 child device - endpoint 2
    def endpointNumber = 2
    def result
    if (commands instanceof String) {
        commands = commands.split(',') as List
    }
    result = commands.collect { encap(endpointNumber, it) }
    sendHubCommand(result, 100)
}

def encap(endpointNumber, cmd) {
    if (cmd instanceof physicalgraph.zwave.Command) {
        command(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint: endpointNumber).encapsulate(cmd))
    } else if (cmd.startsWith("delay")) {
        cmd
    } else {
        def header = "600D00"
        String.format("%s%02X%s", header, endpointNumber, cmd)
    }
}

private command(physicalgraph.zwave.Command cmd) {
    if (zwaveInfo.zw.contains("s")) {
        secEncap(cmd)
    } else if (zwaveInfo.cc.contains("56")){
        crcEncap(cmd)
    } else {
        cmd.format()
    }
}

private secEncap(physicalgraph.zwave.Command cmd) {
    zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}

private crcEncap(physicalgraph.zwave.Command cmd) {
    zwave.crc16EncapV1.crc16Encap().encapsulate(cmd).format()
}