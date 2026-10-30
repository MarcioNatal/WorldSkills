# CAN Bus Network 

## Controller Area Network

CAN bus is a two wire high speed network that is designed to facilitate communication between multiple devices on your robot. It is recommended that CAN on your robot follow a “daisy-chain” topology. This means that the CAN wiring should usually start at your roboRIO and go into and out of each device successively until finally ending at the PDP.

:rocket: **High Speed Data Bus 1Mbps.**

### 1. STANDARD SIGNALS

- :point_down: Below are the waveforms for boths signals: CAN High and CAN Low

- Logic level is set by the differencial voltage between CAN High and CAN Low.

<img width="573" height="559" alt="image" src="https://github.com/user-attachments/assets/fd8d57c9-84bf-46b3-af93-423accf94a9c" />

#### 1.1 Waveform in case signals are shorted.

<img width="445" height="401" alt="image" src="https://github.com/user-attachments/assets/7c588568-70b7-4e8b-b8ea-9d380c0c9117" />

#### 1.2 MULTIMETER CHECK

#### MEAN VOLTAGE
| CAN HIGH | CAN LOW |
| :---:     | :---:  |
| 2.6 V    | 2.1V    |

| EQUIVALENT RESISTANCE |
| :----------------:    |
|        60 Ω           |


### 2. TOPOLOGY

#### Standard Wiring
CAN bus is generally wired with yellow and green wire with yellow acting as the CAN-High and green as the CAN-Low signals. Many devices show this yellow and green color scheme to indicate how the wires should be plugged in.

#### Terminating resistor

It is recommended that the wiring starts at the roboRIO and ends at the PDP because the CAN network is required to be terminated by 120 Ω resistors and these are built into these two devices. The PDP ships with the CAN bus terminating resistor jumper in the “ON” position. It is recommended to leave the jumper in this position and place any additional CAN nodes between the roboRIO and the PDP (leaving the PDP as the end of the bus). If you wish to place the PDP in the middle of the bus (utilizing both pairs of PDP CAN terminals) move the jumper to the “OFF” position and place your own 120 
terminating resistor at the end of your CAN bus chain.

<img width="975" height="488" alt="image" src="https://github.com/user-attachments/assets/e763a20f-ce7e-4d2a-ae03-c359103bc314" />

### Daisy chain topology is recommended :+1:

<img width="975" height="413" alt="image" src="https://github.com/user-attachments/assets/e59988ea-b967-4794-bee3-e82c2fe6e3d6" />

### Star topology -  Stubs should be smaller then 30 cm :no_good: :-1:

- Stubs greater than 30 cm can reflect signals back

<img width="975" height="636" alt="image" src="https://github.com/user-attachments/assets/98b8e5f0-5370-4d42-b426-98a50058c8b0" />


### CANivore topology

<img width="975" height="920" alt="image" src="https://github.com/user-attachments/assets/1ffb6836-0db4-4330-90e3-6a70ea45887c" />

## Troubleshooting

### 1. Check terminating resistor

- Connect multimeter in any device - the expected Resistance is 60 Ω
- If Resistance is 120 Ω , so circuit is open.

<img width="975" height="493" alt="image" src="https://github.com/user-attachments/assets/a47afe24-d111-4295-b234-ada193084faa" />


### 2. CAN H and CAN L connected backward

- Measure Voltage between CAN H and GND and same with CAN L
  
- VCAN H > VCAN L is expected

#### Values out of expected

<img width="1157" height="585" alt="image" src="https://github.com/user-attachments/assets/59ee5f22-a783-4da3-b633-fcc1a780200d" />

### 3. Missing CAN bus signal

- Measure shows CAN H is open.

<img width="1288" height="560" alt="image" src="https://github.com/user-attachments/assets/bb42eaf5-81be-406f-b5aa-628928ca4075" />

### 4.	CAN bus signal ground shorted

- At first both voltages indicate 0V due to terminating resistor.

- Disconnect terminating resistors to isolate signals and then check voltages again

- CAN bus that remains at 0V is shorted.

<img width="935" height="511" alt="image" src="https://github.com/user-attachments/assets/adc959fe-a6cd-4bea-89ad-e1dbe5802703" />





  


