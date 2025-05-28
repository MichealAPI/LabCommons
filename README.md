<div align="left" style="position: relative;">
<img src="/logo.png" align="right" style="margin: -20px 0 0 20px;" alt="logo">
<br>

<p align="left">
	<img src="https://img.shields.io/github/license/MichealAPI/LabCommons?style=default&logo=opensourceinitiative&logoColor=white&color=00c5ff" alt="license">
	<img src="https://img.shields.io/github/last-commit/MichealAPI/LabCommons?style=default&logo=git&logoColor=white&color=00c5ff" alt="last-commit">
	<img src="https://img.shields.io/github/languages/top/MichealAPI/LabCommons?style=default&color=00c5ff" alt="repo-top-language">
	<img src="https://img.shields.io/github/languages/count/MichealAPI/LabCommons?style=default&color=00c5ff" alt="repo-language-count">
</p>
</div>
<br clear="right">

## 🔗 Table of Contents

- [📍 Overview](#-overview)
- [👾 Features](#-features)
- [📁 Project Structure](#-project-structure)
- [📂 Project Index](#-project-index)
- [🚀 Getting Started](#-getting-started)
- [☑️ Prerequisites](#-prerequisites)
- [⚙️ Installation](#-installation)
- [🤖 Usage](#-usage)
- [🧪 Testing](#-testing)
- [📌 Project Roadmap](#-project-roadmap)
- [🔰 Contributing](#-contributing)
- [🎗 License](#-license)

---

## 📍 Overview

This project is a lightweight and modular utility library for Minecraft plugin developers. It simplifies common tasks like chat formatting, GUI creation, and database integration across multiple platforms such as Spigot and Velocity.

Designed to save development time, it offers powerful utilities for plugin messaging, custom inventories, and general-purpose functions. Whether you're building a mini-game or a large network plugin, this library streamlines your workflow.

---

## 👾 Features

- Utilities for chat messaging and formatting
- Cross-platform plugin support
- Compatibility with multiple databases
- Interactive and highly customizable GUIs
- Handy string, hex, and general-purpose utilities

---

## 📁 Project Structure

```sh
└── LabCommons/
    ├── .github
    │   └── workflows
    │       └── publish.yml
    ├── LICENSE
    ├── README.md
    ├── pom.xml
    └── src
        ├── main
        │   ├── java
        │   └── resources
        └── test
            ├── java
            └── resources
```


### 📂 Project Index
<details open>
	<summary><b><code>LABCOMMONS/</code></b></summary>
	<details> <!-- __root__ Submodule -->
		<summary><b>__root__</b></summary>
		<blockquote>
			<table>
			</table>
		</blockquote>
	</details>
	<details> <!-- .github Submodule -->
		<summary><b>.github</b></summary>
		<blockquote>
			<details>
				<summary><b>workflows</b></summary>
				<blockquote>
					<table>
					<tr>
						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/.github/workflows/publish.yml'>publish.yml</a></b></td>
						<td><code>❯ Workflow for Publishing to the Repo</code></td>
					</tr>
					</table>
				</blockquote>
			</details>
		</blockquote>
	</details>
	<details> <!-- src Submodule -->
		<summary><b>src</b></summary>
		<blockquote>
			<details>
				<summary><b>main</b></summary>
				<blockquote>
					<details>
						<summary><b>resources</b></summary>
						<blockquote>
							<table>
							<tr>
								<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/resources/plugin.yml'>plugin.yml</a></b></td>
								<td><code>❯ Minecraft Plugin Settings</code></td>
							</tr>
							</table>
						</blockquote>
					</details>
					<details>
						<summary><b>java</b></summary>
						<blockquote>
							<details>
								<summary><b>it</b></summary>
								<blockquote>
									<details>
										<summary><b>mikeslab</b></summary>
										<blockquote>
											<details>
												<summary><b>commons</b></summary>
												<blockquote>
													<table>
													<tr>
														<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/LabCommons.java'>LabCommons.java</a></b></td>
														<td><code>❯ Main Class</code></td>
													</tr>
													</table>
													<details>
														<summary><b>api</b></summary>
														<blockquote>
															<details>
																<summary><b>config</b></summary>
																<blockquote>
																	<table>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/config/ConfigurableEnum.java'>ConfigurableEnum.java</a></b></td>
																		<td><code>❯ Allows Key-Based Configuration</code></td>
																	</tr>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/config/Configurable.java'>Configurable.java</a></b></td>
																		<td><code>❯ Custom Configuration Handler</code></td>
																	</tr>
																	</table>
																	<details>
																		<summary><b>impl</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/config/impl/ConfigurableImpl.java'>ConfigurableImpl.java</a></b></td>
																				<td><code>❯ Custom Configuration Handler Impl</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																</blockquote>
															</details>
															<details>
																<summary><b>chat</b></summary>
																<blockquote>
																	<table>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/chat/ChatMessagingHandler.java'>ChatMessagingHandler.java</a></b></td>
																		<td><code>❯ Chat Messaging made easy</code></td>
																	</tr>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/chat/ChatMessagingContext.java'>ChatMessagingContext.java</a></b></td>
																		<td><code>❯ Context of the Chat</code></td>
																	</tr>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/chat/ChatMessagingListener.java'>ChatMessagingListener.java</a></b></td>
																		<td><code>❯ Chat Listener</code></td>
																	</tr>
																	</table>
																</blockquote>
															</details>
															<details>
																<summary><b>inventory</b></summary>
																<blockquote>
																	<table>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/CustomInventory.java'>CustomInventory.java</a></b></td>
																		<td><code>❯ Helps translating from Bukkit Inventories to GUIs</code></td>
																	</tr>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/CustomGui.java'>CustomGui.java</a></b></td>
																		<td><code>❯ GUIs Impl</code></td>
																	</tr>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/GuiType.java'>GuiType.java</a></b></td>
																		<td><code>❯ Identifies multiple types of GUIs</code></td>
																	</tr>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/InventoryType.java'>InventoryType.java</a></b></td>
																		<td><code>❯ Identifies multiple types of Inventories</code></td>
																	</tr>
																	</table>
																	<details>
																		<summary><b>config</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/config/GuiConfig.java'>GuiConfig.java</a></b></td>
																				<td><code>❯ Translates a config to a working GUI</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/config/ConfigField.java'>ConfigField.java</a></b></td>
																				<td><code>❯ Customizable fields</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/config/ConditionParser.java'>ConditionParser.java</a></b></td>
																				<td><code>❯ Parses condition-based customizations</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/config/GuiConfigImpl.java'>GuiConfigImpl.java</a></b></td>
																				<td><code>❯ GUI from Config impl</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>factory</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/factory/GuiFactory.java'>GuiFactory.java</a></b></td>
																				<td><code>❯ Interface for the GUI factory</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/factory/GuiFactoryImpl.java'>GuiFactoryImpl.java</a></b></td>
																				<td><code>❯ GUI Factory Implementation</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>helper</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/helper/ActionHelper.java'>ActionHelper.java</a></b></td>
																				<td><code>❯ Helper for Actions performable in a GUI</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/helper/InventoryMap.java'>InventoryMap.java</a></b></td>
																				<td><code>❯ Cache Handler</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>event</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/event/GuiListener.java'>GuiListener.java</a></b></td>
																				<td><code>❯ GUI Event Listener</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/event/GuiEvent.java'>GuiEvent.java</a></b></td>
																				<td><code>❯ Custom GUI Events</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/event/GuiInteractEvent.java'>GuiInteractEvent.java</a></b></td>
																				<td><code>❯ Custom GUI Event</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>pojo</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/pojo/GuiDetails.java'>GuiDetails.java</a></b></td>
																				<td><code>❯ GUI Specifics</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/pojo/GuiContext.java'>GuiContext.java</a></b></td>
																				<td><code>❯ GUI useful data POJO</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/pojo/GuiElement.java'>GuiElement.java</a></b></td>
																				<td><code>❯ GUI Item POJO</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/pojo/Animation.java'>Animation.java</a></b></td>
																				<td><code>❯ GUI Animation frame</code></td>
																			</tr>
																			</table>
																			<details>
																				<summary><b>action</b></summary>
																				<blockquote>
																					<table>
																					<tr>
																						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/pojo/action/GuiAction.java'>GuiAction.java</a></b></td>
																						<td><code>❯ GUI Action</code></td>
																					</tr>
																					</table>
																				</blockquote>
																			</details>
																			<details>
																				<summary><b>population</b></summary>
																				<blockquote>
																					<table>
																					<tr>
																						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/pojo/population/PopulatePageContext.java'>PopulatePageContext.java</a></b></td>
																						<td><code>❯ GUI Page Context</code></td>
																					</tr>
																					<tr>
																						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/pojo/population/InventoryPopulationContext.java'>InventoryPopulationContext.java</a></b></td>
																						<td><code>❯ ...</code></td>
																					</tr>
																					<tr>
																						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/pojo/population/PopulateRowContext.java'>PopulateRowContext.java</a></b></td>
																						<td><code>❯ ...</code></td>
																					</tr>
																					</table>
																				</blockquote>
																			</details>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>util</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/util/PageSystem.java'>PageSystem.java</a></b></td>
																				<td><code>❯ Page System Impl</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/util/MappingUtil.java'>MappingUtil.java</a></b></td>
																				<td><code>❯ Utility for Mapping</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/util/ConditionUtil.java'>ConditionUtil.java</a></b></td>
																				<td><code>❯ Utility for Conditions</code></td>
																			</tr>
																			</table>
																			<details>
																				<summary><b>config</b></summary>
																				<blockquote>
																					<table>
																					<tr>
																						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/util/config/FileUtil.java'>FileUtil.java</a></b></td>
																						<td><code>❯ File Util</code></td>
																					</tr>
																					<tr>
																						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/util/config/GuiChecker.java'>GuiChecker.java</a></b></td>
																						<td><code>❯ Checks a GUI status and corruptness</code></td>
																					</tr>
																					</table>
																				</blockquote>
																			</details>
																			<details>
																				<summary><b>animation</b></summary>
																				<blockquote>
																					<table>
																					<tr>
																						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/util/animation/AnimationUtil.java'>AnimationUtil.java</a></b></td>
																						<td><code>❯ Animation Util</code></td>
																					</tr>
																					<tr>
																						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/util/animation/FrameColorUtil.java'>FrameColorUtil.java</a></b></td>
																						<td><code>❯ ...</code></td>
																					</tr>
																					</table>
																				</blockquote>
																			</details>
																			<details>
																				<summary><b>action</b></summary>
																				<blockquote>
																					<table>
																					<tr>
																						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/util/action/ActionRegistrar.java'>ActionRegistrar.java</a></b></td>
																						<td><code>❯ Registers custom Actions</code></td>
																					</tr>
																					<tr>
																						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/util/action/ActionHandler.java'>ActionHandler.java</a></b></td>
																						<td><code>❯ Interface for handling Actions</code></td>
																					</tr>
																					<tr>
																						<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/util/action/ActionHandlerImpl.java'>ActionHandlerImpl.java</a></b></td>
																						<td><code>❯ Impl for handling Actions</code></td>
																					</tr>
																					</table>
																					<details>
																						<summary><b>internal</b></summary>
																						<blockquote>
																							<table>
																							<tr>
																								<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/inventory/util/action/internal/ConsumerFilter.java'>ConsumerFilter.java</a></b></td>
																								<td><code>❯ Filters</code></td>
																							</tr>
																							</table>
																						</blockquote>
																					</details>
																				</blockquote>
																			</details>
																		</blockquote>
																	</details>
																</blockquote>
															</details>
															<details>
																<summary><b>formatter</b></summary>
																<blockquote>
																	<table>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/formatter/FormatUtil.java'>FormatUtil.java</a></b></td>
																		<td><code>❯ Formatting Util</code></td>
																	</tr>
																	</table>
																</blockquote>
															</details>
															<details>
																<summary><b>logger</b></summary>
																<blockquote>
																	<table>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/logger/LogUtils.java'>LogUtils.java</a></b></td>
																		<td><code>❯ Log Util</code></td>
																	</tr>
																	</table>
																</blockquote>
															</details>
															<details>
																<summary><b>component</b></summary>
																<blockquote>
																	<table>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/component/ComponentsUtil.java'>ComponentsUtil.java</a></b></td>
																		<td><code>❯ Kyori Components Util</code></td>
																	</tr>
																	</table>
																</blockquote>
															</details>
															<details>
																<summary><b>various</b></summary>
																<blockquote>
																	<table>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/StringUtil.java'>StringUtil.java</a></b></td>
																		<td><code>❯ Stil Util</code></td>
																	</tr>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/LabYaml.java'>LabYaml.java</a></b></td>
																		<td><code>❯ Yaml Loader</code></td>
																	</tr>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/HexUtils.java'>HexUtils.java</a></b></td>
																		<td><code>❯ Hex Utils</code></td>
																	</tr>
																	</table>
																	<details>
																		<summary><b>delay</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/delay/DelayedAction.java'>DelayedAction.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/delay/DelayHandler.java'>DelayHandler.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/delay/DelayHandlerImpl.java'>DelayHandlerImpl.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>item</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/item/SkullUtil.java'>SkullUtil.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/item/ItemCreator.java'>ItemCreator.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>message</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/message/MessageHelper.java'>MessageHelper.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/message/MessageHelperImpl.java'>MessageHelperImpl.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>platform</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/platform/PlatformUtil.java'>PlatformUtil.java</a></b></td>
																				<td><code>❯ Cross-Platform utiil</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/various/platform/Platform.java'>Platform.java</a></b></td>
																				<td><code>❯ Platform POJO</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																</blockquote>
															</details>
															<details>
																<summary><b>database</b></summary>
																<blockquote>
																	<table>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/Database.java'>Database.java</a></b></td>
																		<td><code>❯ Database Interface</code></td>
																	</tr>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/SupportedDatabase.java'>SupportedDatabase.java</a></b></td>
																		<td><code>❯ ...</code></td>
																	</tr>
																	<tr>
																		<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/SerializableMapConvertible.java'>SerializableMapConvertible.java</a></b></td>
																		<td><code>❯ From Database Record to POJO through Maps</code></td>
																	</tr>
																	</table>
																	<details>
																		<summary><b>config</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/config/ConfigDatabaseUtil.java'>ConfigDatabaseUtil.java</a></b></td>
																				<td><code>❯ From Config to Database</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>async</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/async/AsyncDatabaseImpl.java'>AsyncDatabaseImpl.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/async/AsyncDatabase.java'>AsyncDatabase.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>impl</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/impl/MongoDatabaseImpl.java'>MongoDatabaseImpl.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/impl/JSONDatabaseImpl.java'>JSONDatabaseImpl.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/impl/SQLDatabaseImpl.java'>SQLDatabaseImpl.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>pojo</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/pojo/URIBuilder.java'>URIBuilder.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>util</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/util/SimpleMapConvertible.java'>SimpleMapConvertible.java</a></b></td>
																				<td><code>❯ Simplifies SerializableMapConvertible for a casual usage</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/util/SQLUtil.java'>SQLUtil.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/main/java/it/mikeslab/commons/api/database/util/PojoMapper.java'>PojoMapper.java</a></b></td>
																				<td><code>❯ From Map to POJO</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																</blockquote>
															</details>
														</blockquote>
													</details>
												</blockquote>
											</details>
										</blockquote>
									</details>
								</blockquote>
							</details>
						</blockquote>
					</details>
				</blockquote>
			</details>
			<details>
				<summary><b>test</b></summary>
				<blockquote>
					<details>
						<summary><b>resources</b></summary>
						<blockquote>
							<table>
							<tr>
								<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/test/resources/test.json'>test.json</a></b></td>
								<td><code>❯ Test Data</code></td>
							</tr>
							</table>
						</blockquote>
					</details>
					<details>
						<summary><b>java</b></summary>
						<blockquote>
							<details>
								<summary><b>it</b></summary>
								<blockquote>
									<details>
										<summary><b>mikeslab</b></summary>
										<blockquote>
											<details>
												<summary><b>commons</b></summary>
												<blockquote>
													<details>
														<summary><b>api</b></summary>
														<blockquote>
															<details>
																<summary><b>database</b></summary>
																<blockquote>
																	<details>
																		<summary><b>impl</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/test/java/it/mikeslab/commons/api/database/impl/DatabaseTest.java'>DatabaseTest.java</a></b></td>
																				<td><code>❯ Test Units for Databases</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>helper</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/test/java/it/mikeslab/commons/api/database/helper/DatabaseTestHelper.java'>DatabaseTestHelper.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>pojo</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/test/java/it/mikeslab/commons/api/database/pojo/TestObject.java'>TestObject.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																	<details>
																		<summary><b>util</b></summary>
																		<blockquote>
																			<table>
																			<tr>
																				<td><b><a href='https://github.com/MichealAPI/LabCommons/blob/master/src/test/java/it/mikeslab/commons/api/database/util/URIUtil.java'>URIUtil.java</a></b></td>
																				<td><code>❯ ...</code></td>
																			</tr>
																			</table>
																		</blockquote>
																	</details>
																</blockquote>
															</details>
														</blockquote>
													</details>
												</blockquote>
											</details>
										</blockquote>
									</details>
								</blockquote>
							</details>
						</blockquote>
					</details>
				</blockquote>
			</details>
		</blockquote>
	</details>
</details>

---
## 🚀 Getting Started

### ☑️ Prerequisites

Before getting started with LabCommons, ensure your runtime environment meets the following requirements:

- **Programming Language:** Java 1.8


### ⚙️ Installation

Install LabCommons using one of the following methods:

**Build from source:**

1. Clone the LabCommons repository:
```sh
❯ git clone https://github.com/MichealAPI/LabCommons
```

2. Navigate to the project directory:
```sh
❯ cd LabCommons
```

3. Load the Maven script and you're ready!
```sh
❯ mvn clean install
```


### 🤖 Usage
Add LabCommons as a dependency using Maven or Gradle.
Check out our **[repository](https://repo.mikeslab.it/#/)** for the latest version.

```xml
<repository>
  <id>mikes-repo</id>
  <name>Mike's Laboratory</name>
  <url>https://repo.mikeslab.it/repository</url>
</repository>
```


### 🧪 Testing
Run the test suite using the following command:
echo 'mvn test'

---
## 📌 Project Roadmap

- [ ] **`Task 1`**: Enhance Minecraft v1.8.x compatibility

---

## 🔰 Contributing

- **💬 [Join the Discussions](https://github.com/MichealAPI/LabCommons/discussions)**: Share your insights, provide feedback, or ask questions.
- **🐛 [Report Issues](https://github.com/MichealAPI/LabCommons/issues)**: Submit bugs found or log feature requests for the `LabCommons` project.
- **💡 [Submit Pull Requests](https://github.com/MichealAPI/LabCommons/blob/main/CONTRIBUTING.md)**: Review open PRs, and submit your own PRs.

<details closed>
<summary>Contributing Guidelines</summary>

1. **Fork the Repository**: Start by forking the project repository to your github account.
2. **Clone Locally**: Clone the forked repository to your local machine using a git client.
   ```sh
   git clone https://github.com/MichealAPI/LabCommons
   ```
3. **Create a New Branch**: Always work on a new branch, giving it a descriptive name.
   ```sh
   git checkout -b new-feature-x
   ```
4. **Make Your Changes**: Develop and test your changes locally.
5. **Commit Your Changes**: Commit with a clear message describing your updates.
   ```sh
   git commit -m 'Implemented new feature x.'
   ```
6. **Push to github**: Push the changes to your forked repository.
   ```sh
   git push origin new-feature-x
   ```
7. **Submit a Pull Request**: Create a PR against the original project repository. Clearly describe the changes and their motivations.
8. **Review**: Once your PR is reviewed and approved, it will be merged into the main branch. Congratulations on your contribution!
</details>

<details closed>
<summary>Contributor Graph</summary>
<br>
<p align="left">
   <a href="https://github.com{/MichealAPI/LabCommons/}graphs/contributors">
      <img src="https://contrib.rocks/image?repo=MichealAPI/LabCommons">
   </a>
</p>
</details>

---

## 🎗 License

This project is protected under the [GNU GPLv3](https://www.gnu.org/licenses/gpl-3.0.html) License. For more details, refer to the [LICENSE](https://www.gnu.org/licenses/gpl-3.0.html) file.

---
