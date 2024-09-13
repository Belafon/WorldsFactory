import logging

logging.basicConfig(filename='story.log', encoding='utf-8', level=logging.INFO, format='%(asctime)s %(message)s', datefmt='%m/%d/%Y %I:%M:%S %p')
logging.info('Loading python code')
from collections import UserList

class CustomList(UserList):
	def __init__(self, *args, index, name, type):
		super().__init__(*args)
		self.index = index if index is not None else ()
		self.name = name
		self.type = type if type is not None else ""
		length = len(args)
		self.dimensions = "[" + str(length) + "]" if length > 0 else "[0]"
	
	direct_access = False
	def __setitem__(self, index, value):
		indexes = self.index
		indexes += (index,)
		super().__setitem__(index, value)
		if not self.direct_access:
			set_array_property(self.name, value, self.type, indexes, self.__object_name__)
		else:
			self.direct_access = False
	
	def set_objects_name(self, name):
		self.__object_name__ = name
		pass

class ArrayObject:
	def __init__(self, shape, name, type):
		self._my_array = self._initialize_array(shape, (), name, type)
		self.dimensions = shape
	
	def _initialize_array(self, shape, index, name, type):
		if len(shape) == 1:
			self.length = shape[0]
			return CustomList([None] * shape[0], index=index, name=name, type=type)
		else:
			return CustomList([self._initialize_array(shape[1:], index + (i,), name, type) for i in range(shape[0])], index=index, name=name, type=type)
	
	def __getitem__(self, index):
		return self._get_value(self._my_array, index)
	
	def __setitem__(self, index, value):
		self._set_value(self._my_array, index, value)
	
	def _get_value(self, array, index):
		if isinstance(index, int):
			return array[index]
		else:
			return self._get_value(array[index[0]], index[1:])
	
	def _set_value(self, array, index, value):
		if isinstance(index, int):
			array[index] = value
		else:
			self._set_value(array[index[0]], index[1:], value)
	
	def set_objects_name(self, name):
		self.__object_name__ = name
		if isinstance(self._my_array, CustomList):
			self._my_array.set_objects_name(name)
		if self._my_array is not None:
			for i in self._my_array:
				if isinstance(i, CustomList):
					i.set_objects_name(name)
		pass

# ------------------- PROPERTY SETTER -------------------

class LibraryEntryNotSetException(Exception):
	def __init__(self, message="library is not set"):
		self.message = message
		super().__init__(self.message)

def set_property(propertyName, value, type, objectName):
	print('_set_property_')
	print(propertyName)
	print(type)
	print(objectName)
	print(str(value))
	print('_set_property_end_')

def set_array_property(propertyName, value, type, index, objectName):
	print('_set_array_property_')
	print(propertyName)
	print(type)
	print(objectName)
	print(str(value))
	print(str(index))
	print('_set_array_property_end_')

# ------------------- LIBRARY CLASSES -------------------

class MethodRuntimeException(Exception):
	def __init__(self, message="An exception occurred during method execution"):
		self.message = message
		super().__init__(self.message)

class class_StoryOption:
	def __init__(self, id):
		self._id = id
		
	properties = {
		"__object_name__": "@basicType:String",
		"title": "@basicType:String"
	}
	@property
	def title(self):
		return self._title
		pass
	@title.setter
	def title(self, value):
		self._title = value
		set_property("title", value, self.properties["title"], self.__object_name__)
	
	def __str__(self):
		return self._id
	
	pass


class class_StoryMessage:
	def __init__(self, id):
		self._id = id
		self.options = ArrayObject([10], "options", "@basicType:String")
		
	properties = {
		"__object_name__": "@basicType:String",
		"message": "@basicType:String",
		"options": "@basicType:String"
	}
	@property
	def message(self):
		return self._message
		pass
	@message.setter
	def message(self, value):
		self._message = value
		set_property("message", value, self.properties["message"], self.__object_name__)
	
	def __str__(self):
		return self._id
	
	def clearOptionsArray(self):
		logging.info("run method ---->   @class:StoryMessage.@method:StoryMessage_clearOptionsArray")
		try:
			return self._clearOptionsArray()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:StoryMessage"
			method = "@method:StoryMessage_clearOptionsArray"
			raise MethodRuntimeException("An Exception in @class:StoryMessage during method @method:StoryMessage_clearOptionsArray\n@class:StoryMessage\n@method:StoryMessage_clearOptionsArray\n" + message)
	
	def _clearOptionsArray(self):
		for i in range(0, self.options.length - 1):
			self.options[i] = None
		pass
		pass
	pass


class class_Place:
	def __init__(self, id):
		self._id = id
		
	properties = {
		"__object_name__": "@basicType:String",
		"x": "@basicType:Integer",
		"y": "@basicType:Integer",
		"description": "@basicType:String",
		"weather": "@basicType:String",
		"clouds": "@basicType:String"
	}
	@property
	def x(self):
		return self._x
		pass
	@x.setter
	def x(self, value):
		self._x = value
		set_property("x", value, self.properties["x"], self.__object_name__)
	
	@property
	def y(self):
		return self._y
		pass
	@y.setter
	def y(self, value):
		self._y = value
		set_property("y", value, self.properties["y"], self.__object_name__)
	
	@property
	def description(self):
		return self._description
		pass
	@description.setter
	def description(self, value):
		self._description = value
		set_property("description", value, self.properties["description"], self.__object_name__)
	
	@property
	def weather(self):
		return self._weather
		pass
	@weather.setter
	def weather(self, value):
		self._weather = value
		set_property("weather", value, self.properties["weather"], self.__object_name__)
	
	@property
	def clouds(self):
		return self._clouds
		pass
	@clouds.setter
	def clouds(self, value):
		self._clouds = value
		set_property("clouds", value, self.properties["clouds"], self.__object_name__)
	
	def __str__(self):
		return self._id
	
	pass


class class_Story:
	def __init__(self, id):
		self._id = id
		
	properties = {
		"__object_name__": "@basicType:String",
		"lastMessage": "@class:StoryMessage",
		"choosenOption": "@basicType:String"
	}
	@property
	def lastMessage(self):
		return self._lastMessage
		pass
	@lastMessage.setter
	def lastMessage(self, value):
		self._lastMessage = value
		set_property("lastMessage", value, self.properties["lastMessage"], self.__object_name__)
	
	@property
	def choosenOption(self):
		return self._choosenOption
		pass
	@choosenOption.setter
	def choosenOption(self, value):
		self._choosenOption = value
		set_property("choosenOption", value, self.properties["choosenOption"], self.__object_name__)
	
	def __str__(self):
		return self._id
	
	pass


class class_Time:
	def __init__(self, id):
		self._id = id
		
	properties = {
		"__object_name__": "@basicType:String",
		"currentPartOfDay": "@basicType:String"
	}
	@property
	def currentPartOfDay(self):
		return self._currentPartOfDay
		pass
	@currentPartOfDay.setter
	def currentPartOfDay(self, value):
		self._currentPartOfDay = value
		set_property("currentPartOfDay", value, self.properties["currentPartOfDay"], self.__object_name__)
	
	def __str__(self):
		return self._id
	
	pass


class class_Creature:
	def __init__(self, id):
		self._id = id
		self.informations = ArrayObject([10], "informations", "@basicType:String")
		self.inventory = ArrayObject([10], "inventory", "@class:Item")
		
	properties = {
		"__object_name__": "@basicType:String",
		"location": "@class:Place",
		"informations": "@basicType:String",
		"inventory": "@class:Item"
	}
	@property
	def location(self):
		return self._location
		pass
	@location.setter
	def location(self, value):
		self._location = value
		set_property("location", value, self.properties["location"], self.__object_name__)
	
	def __str__(self):
		return self._id
	
	def addInformation(self, information):
		logging.info("run method ---->   @class:Creature.@method:Creature_addInformation")
		try:
			return self._addInformation( information)
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:Creature"
			method = "@method:Creature_addInformation"
			raise MethodRuntimeException("An Exception in @class:Creature during method @method:Creature_addInformation\n@class:Creature\n@method:Creature_addInformation\n" + message)
	
	def _addInformation(self, information):
		for i, value in enumerate(self.informations):
			if value is None:
				self.informations[i] = information
				break
		pass
		pass
	
	def isTypeOfItemInInventory(self, classType):
		logging.info("run method ---->   @class:Creature.@method:Creature_isTypeOfItemInInventory")
		try:
			return self._isTypeOfItemInInventory( classType)
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:Creature"
			method = "@method:Creature_isTypeOfItemInInventory"
			raise MethodRuntimeException("An Exception in @class:Creature during method @method:Creature_isTypeOfItemInInventory\n@class:Creature\n@method:Creature_isTypeOfItemInInventory\n" + message)
	
	def _isTypeOfItemInInventory(self, classType):
		for item in self.inventory:
			if isinstance(item, classType):
				return True
		return False
		pass
	
	def addItemToInventory(self, item):
		logging.info("run method ---->   @class:Creature.@method:Creature_addItemToInventory")
		try:
			return self._addItemToInventory( item)
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:Creature"
			method = "@method:Creature_addItemToInventory"
			raise MethodRuntimeException("An Exception in @class:Creature during method @method:Creature_addItemToInventory\n@class:Creature\n@method:Creature_addItemToInventory\n" + message)
	
	def _addItemToInventory(self, item):
		for i, value in enumerate(self.inventory):
			if value is None:
				self.inventory[i] = item
				break
		pass
		pass
	pass


class class_Player(class_Creature):
	def __init__(self, id):
		self._id = id
		self.informations = ArrayObject([10], "informations", "@basicType:String")
		self.inventory = ArrayObject([10], "inventory", "@class:Item")
		
	properties = {
		**class_Creature.properties,
		"__object_name__": "@basicType:String",
	}
	@property
	def location(self):
		return self._location
		pass
	@location.setter
	def location(self, value):
		self._location = value
		set_property("location", value, self.properties["location"], self.__object_name__)
	
	pass
	def __str__(self):
		return self._id
	
	pass


class class_Map:
	def __init__(self, id):
		self._id = id
		self.places = ArrayObject([2,3], "places", "@class:Place")
		
	properties = {
		"__object_name__": "@basicType:String",
		"places": "@class:Place"
	}
	def __str__(self):
		return self._id
	
	pass


class class_Item:
	def __init__(self, id):
		self._id = id
		
	properties = {
		"__object_name__": "@basicType:String",
		"location": "@class:Place"
	}
	@property
	def location(self):
		return self._location
		pass
	@location.setter
	def location(self, value):
		self._location = value
		set_property("location", value, self.properties["location"], self.__object_name__)
	
	def __str__(self):
		return self._id
	
	pass


class class_Apple(class_Item):
	def __init__(self, id):
		self._id = id
		
	properties = {
		**class_Item.properties,
		"__object_name__": "@basicType:String",
	}
	@property
	def location(self):
		return self._location
		pass
	@location.setter
	def location(self, value):
		self._location = value
		set_property("location", value, self.properties["location"], self.__object_name__)
	
	pass
	def __str__(self):
		return self._id
	
	def initialize(self):
		logging.info("run method ---->   @class:Apple.@method:Apple_initialize")
		try:
			return self._initialize()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:Apple"
			method = "@method:Apple_initialize"
			raise MethodRuntimeException("An Exception in @class:Apple during method @method:Apple_initialize\n@class:Apple\n@method:Apple_initialize\n" + message)
	
	def _initialize(self):
		pass
		pass
	pass


class class_Coin(class_Item):
	def __init__(self, id):
		self._id = id
		
	properties = {
		**class_Item.properties,
		"__object_name__": "@basicType:String",
	}
	@property
	def location(self):
		return self._location
		pass
	@location.setter
	def location(self, value):
		self._location = value
		set_property("location", value, self.properties["location"], self.__object_name__)
	
	pass
	def __str__(self):
		return self._id
	
	def initialize(self):
		logging.info("run method ---->   @class:Coin.@method:Coin_initialize")
		try:
			return self._initialize()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:Coin"
			method = "@method:Coin_initialize"
			raise MethodRuntimeException("An Exception in @class:Coin during method @method:Coin_initialize\n@class:Coin\n@method:Coin_initialize\n" + message)
	
	def _initialize(self):
		pass
		pass
	pass



# ------------------- LIBRARY OBJECTS -------------------

class Objects:
	_instance = None
	
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(Objects, cls).__new__(cls)		
		cls.goldenCoin = class_Coin("@object:goldenCoin")
		cls.goldenCoin.__object_name__ = "goldenCoin"
		cls.lumberjack = class_Creature("@object:lumberjack")
		cls.lumberjack.__object_name__ = "lumberjack"
		cls.lumberjack.informations.set_objects_name("lumberjack")
		cls.lumberjack.inventory.set_objects_name("lumberjack")
		cls.wolf = class_Creature("@object:wolf")
		cls.wolf.__object_name__ = "wolf"
		cls.wolf.informations.set_objects_name("wolf")
		cls.wolf.inventory.set_objects_name("wolf")
		cls.mountain_meadow = class_Place("@object:mountain_meadow")
		cls.mountain_meadow.__object_name__ = "mountain_meadow"
		cls.forest_leafy = class_Place("@object:forest_leafy")
		cls.forest_leafy.__object_name__ = "forest_leafy"
		cls.Derek = class_Player("@object:Derek")
		cls.Derek.__object_name__ = "Derek"
		cls.Derek.informations.set_objects_name("Derek")
		cls.Derek.inventory.set_objects_name("Derek")
		cls.aple2 = class_Apple("@object:aple2")
		cls.aple2.__object_name__ = "aple2"
		cls.fisherman = class_Creature("@object:fisherman")
		cls.fisherman.__object_name__ = "fisherman"
		cls.fisherman.informations.set_objects_name("fisherman")
		cls.fisherman.inventory.set_objects_name("fisherman")
		cls.lostSheep = class_Creature("@object:lostSheep")
		cls.lostSheep.__object_name__ = "lostSheep"
		cls.lostSheep.informations.set_objects_name("lostSheep")
		cls.lostSheep.inventory.set_objects_name("lostSheep")
		cls.cliff = class_Place("@object:cliff")
		cls.cliff.__object_name__ = "cliff"
		cls.currentStoryMessage = class_StoryMessage("@object:currentStoryMessage")
		cls.currentStoryMessage.__object_name__ = "currentStoryMessage"
		cls.currentStoryMessage.options.set_objects_name("currentStoryMessage")
		cls.time = class_Time("@object:time")
		cls.time.__object_name__ = "time"
		cls.example = class_Story("@object:example")
		cls.example.__object_name__ = "example"
		cls.apple1 = class_Apple("@object:apple1")
		cls.apple1.__object_name__ = "apple1"
		cls.lake = class_Place("@object:lake")
		cls.lake.__object_name__ = "lake"
		cls.copperCoin = class_Coin("@object:copperCoin")
		cls.copperCoin.__object_name__ = "copperCoin"
		cls.shepherd = class_Creature("@object:shepherd")
		cls.shepherd.__object_name__ = "shepherd"
		cls.shepherd.informations.set_objects_name("shepherd")
		cls.shepherd.inventory.set_objects_name("shepherd")
		cls.story = class_Story("@object:story")
		cls.story.__object_name__ = "story"
		cls.map = class_Map("@object:map")
		cls.map.__object_name__ = "map"
		cls.map.places.set_objects_name("map")
		cls.meadow = class_Place("@object:meadow")
		cls.meadow.__object_name__ = "meadow"
		cls.aple3 = class_Apple("@object:aple3")
		cls.aple3.__object_name__ = "aple3"
		cls.deep_forest = class_Place("@object:deep_forest")
		cls.deep_forest.__object_name__ = "deep_forest"
		try:
			cls.goldenCoin_initialize(cls.goldenCoin)
			cls.lumberjack_initialize(cls.lumberjack)
			cls.wolf_initialize(cls.wolf)
			cls.mountain_meadow_initialize(cls.mountain_meadow)
			cls.forest_leafy_initialize(cls.forest_leafy)
			cls.Derek_initialize(cls.Derek)
			cls.aple2_initialize(cls.aple2)
			cls.fisherman_initialize(cls.fisherman)
			cls.lostSheep_initialize(cls.lostSheep)
			cls.cliff_initialize(cls.cliff)
			cls.currentStoryMessage_initialize(cls.currentStoryMessage)
			cls.time_initialize(cls.time)
			cls.example_initialize(cls.example)
			cls.apple1_initialize(cls.apple1)
			cls.lake_initialize(cls.lake)
			cls.copperCoin_initialize(cls.copperCoin)
			cls.shepherd_initialize(cls.shepherd)
			cls.story_initialize(cls.story)
			cls.map_initialize(cls.map)
			cls.meadow_initialize(cls.meadow)
			cls.aple3_initialize(cls.aple3)
			cls.deep_forest_initialize(cls.deep_forest)
		except Exception as e:
			print("_exception_")
			print("Error in python code, while initializing objects:" + str(e))
			print("_exception_end_")
			raise MethodRuntimeException("An Exception in an init method")
		return cls._instance
		pass
	
	def goldenCoin_initialize(goldenCoin):
		pass
	
	def lumberjack_initialize(lumberjack):
		lumberjack.informations[0] = "Mám hlad"
		pass
	
	def wolf_initialize(wolf):
		pass
	
	def mountain_meadow_initialize(mountain_meadow):
		mountain_meadow.description = "mountain_meadow"
		pass
	
	def forest_leafy_initialize(forest_leafy):
		forest_leafy.description = "forest_leafy"
		pass
	
	def Derek_initialize(Derek):
		pass
	
	def aple2_initialize(aple2):
		pass
	
	def fisherman_initialize(fisherman):
		pass
	
	def lostSheep_initialize(LostSheep):
		pass
	
	def cliff_initialize(cliff):
		cliff.description = "cliff"
		cliff.weather = "idle"
		cliff.clouds = "clear"
		pass
	
	def currentStoryMessage_initialize(currentStoryMessage):
		currentStoryMessage.message = "ahojda"
		pass
	
	def time_initialize(time):
		# possible values...
		# morning, afternoon, sunset_1, night, after_midnight or sunrise
		# for simplicity the type of the currentPartOfDay field is string
		time.currentPartOfDay = "morning"
		pass
	
	def example_initialize(example):
		pass
	
	def apple1_initialize(apple1):
		pass
	
	def lake_initialize(west_forest_leafy):
		west_forest_leafy.description = "lake"
		pass
	
	def copperCoin_initialize(copperCoin):
		pass
	
	def shepherd_initialize(shepherd):
		pass
	
	def story_initialize(story):
		story.choosenOption = "startOption"
		pass
	
	def map_initialize(map):
		pass
	
	def meadow_initialize(meadow):
		meadow.description = "meadow"
		pass
	
	def aple3_initialize(aple3):
		pass
	
	def deep_forest_initialize(deep_forest):
		deep_forest.description = "forest_spurce"
		deep_forest.weather = "idle"
		deep_forest.clouds = "overcast"
		pass
	
	def get_all_object_names_of_type(cls, type_str):
		object_names = []
		# Convert the type string to an actual type object
		try:
			desired_type = eval(type_str)
		except NameError:
			logging.warning(f"Type '{type_str}' is not recognized.")
			return object_names
		for name in dir(cls):
			attr = getattr(cls, name)
			# Check if the attribute is an instance of the desired type
			if isinstance(attr, desired_type):
				object_names.append(name)
		# print all in format name1,name2,name3...
		return ",".join(object_names)

objects = Objects()


# ------------------- EVENTS -------------------


class event_TryToSellApplesToLumberjack:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_TryToSellApplesToLumberjack, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"StartConverzationWithLumberjack",
	]
	
	def loadConditionMethod(self):
		script_path = './TryToSellApplesToLumberjack_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './TryToSellApplesToLumberjack_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:TryToSellApplesToLumberjack.@method:eventsClass_TryToSellApplesToLumberjack_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:TryToSellApplesToLumberjack"
			method = "@method:eventsClass_TryToSellApplesToLumberjack_Condition"
			raise MethodRuntimeException("An Exception in @class:TryToSellApplesToLumberjack during method @method:eventsClass_TryToSellApplesToLumberjack_Condition\n@class:TryToSellApplesToLumberjack\n@method:eventsClass_TryToSellApplesToLumberjack_Condition\n" + message)
	
	def _Condition(self):
		return "Zkusit prodat nějaké jablko" == objects.story.choosenOption
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:TryToSellApplesToLumberjack.@method:eventsClass_TryToSellApplesToLumberjack_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:TryToSellApplesToLumberjack"
			method = "@method:eventsClass_TryToSellApplesToLumberjack_Action"
			raise MethodRuntimeException("An Exception in @class:TryToSellApplesToLumberjack during method @method:eventsClass_TryToSellApplesToLumberjack_Action\n@class:TryToSellApplesToLumberjack\n@method:eventsClass_TryToSellApplesToLumberjack_Action\n" + message)
	
	def _Action(self):
		message = """
		"Nechtěl byste něco k jídlu? Mohl bych vám prodat
		nějaká jablka. Když se tak na vás dívám, tak i za výhodnější 
		cenu. Co říáte?"
		"""
		
		if "Mám hlad" in objects.lumberjack.informations:
			message += """
		"Abych se přiznal, mám docela hlad, ale domů je to docela 
		daleko. Nějakej mědák v kapse mám."
			"""
			
			apple = None # select an apple from inventory
			for i, value in enumerate(objects.Derek.inventory):
				if isinstance(value, class_Apple):
					apple = objects.Derek.inventory[i]
					objects.Derek.inventory[i] = objects.copperCoin
					break
			
			if apple == None:
				message += """
		"Jéje, už žádné nemám, asi jsem ho před chvilkou snědl."
				"""
			else:
				coin = None
				for i, value in enumerate(objects.lumberjack.inventory):
					if isinstance(value, class_Coin):
						#apple = objects.lumberjack.inventory[i]
						#objects.lumberjack.inventory[i] = None
						break
				
				# lumberjack eats the apple immediately
				for i, value in enumerate(objects.lumberjack.informations):
					if value == "Mám hlad":
						objects.lumberjack.informations[i] = None
						break
		else:
			message += """
			"Ani ne, díky."
			"""
		objects.story.lastMessage.clearOptionsArray()
		objects.story.lastMessage.options [0] = "Pokračovat v konverzaci"
		objects.story.lastMessage.message = message
		pass
		pass


class event_Initialize:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_Initialize, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"FirstMeetFisherman",
		"StartConversationWithShepherd",
		"FirstMeetLumberjack",
	]
	
	def loadConditionMethod(self):
		script_path = './Initialize_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './Initialize_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:Initialize.@method:eventsClass_Initialize_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:Initialize"
			method = "@method:eventsClass_Initialize_Condition"
			raise MethodRuntimeException("An Exception in @class:Initialize during method @method:eventsClass_Initialize_Condition\n@class:Initialize\n@method:eventsClass_Initialize_Condition\n" + message)
	
	def _Condition(self):
		return True
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:Initialize.@method:eventsClass_Initialize_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:Initialize"
			method = "@method:eventsClass_Initialize_Action"
			raise MethodRuntimeException("An Exception in @class:Initialize during method @method:eventsClass_Initialize_Action\n@class:Initialize\n@method:eventsClass_Initialize_Action\n" + message)
	
	def _Action(self):
		objects.map.places[0][0] = objects.forest_leafy
		objects.map.places[0][1] = objects.meadow
		objects.map.places[1][0] = objects.mountain_meadow
		objects.map.places[1][1] = objects.lake
		objects.map.places[1][2] = objects.cliff
		objects.map.places[0][2] = objects.deep_forest
		
		objects.story.lastMessage = objects.currentStoryMessage
		
		objects.Derek.inventory[0] = objects.apple1
		objects.lumberjack.inventory[0] = objects.copperCoin
		
		objects.story.lastMessage.clearOptionsArray()
		objects.story.lastMessage.options [0] = "Začít konverzaci"
		objects.story.lastMessage.options [1] = "Jít hledat do lesa"
		objects.story.lastMessage.options [2] = "Jít hledat k jezeru"
		
		objects.story.lastMessage.message = """
		Derek odpovídá obrázku docela běžného obchodníka. 
		Jeho cesty obvykle vedly mezi jihovýchodním pobřežím 
		Geparimu a horských oblastí v severnější části až do měst 
		v úpatí Korunového pohoří.
		
		Minulou zimu se Derek dostal do nesnází, když ho na cestě 
		zastihla nečekaná sněhová vánice. Pastýř, který žil poblíž, 
		mu tehdy nabídl přístřeší a zachránil ho před jistým zmrznutím. 
	
		Nyní, o několik měsíců později, když Derek opět putoval okolo,
		zjistil, že pastýř má problém. Ztratila se mu jedna z jeho ovcí a 
		Derek se rozhodl pastýři pomoci jako poděkování za zimní záchranu. 
		Nabídl se, že ztracenou ovci najde.
		
		Aktuálně se derek nachází na pastvinách spolu s pastýřem...
		"""
		objects.time.currentPartOfDay = "morning"
		# weather can be one of these values:
		#       idle, gentle_rain, rain, heavy_rain, storm
		objects.forest_leafy.weather = "idle"
		objects.meadow.weather = "idle"
		objects.mountain_meadow.weather = "idle"
		objects.lake.weather = "idle"
		# type of clouds can be one of these:
		# clear, few, scattered, overcast
		objects.forest_leafy.clouds = "clear"
		objects.meadow.clouds = "clear"
		objects.mountain_meadow.clouds = "clear"
		objects.lake.clouds = "few"
		# these can be initialized also in 
		# the objects initialize method as it is
		# in the deep_forest object
		objects.shepherd.location = objects.meadow
		objects.Derek.location = objects.meadow
		objects.fisherman.location = objects.lake
		objects.lumberjack.location = objects.forest_leafy
		objects.wolf.location = objects.deep_forest
		objects.lostSheep.location = objects.cliff
		pass
		pass


class event_StartConverzationWithLumberjack:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_StartConverzationWithLumberjack, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"TryToSellApplesToLumberjack",
		"QuestionLumberjackLostSheep",
		"FirstMeetFisherman",
		"LookingForSheepInDeepForest",
		"GoToMeadow",
	]
	
	def loadConditionMethod(self):
		script_path = './StartConverzationWithLumberjack_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './StartConverzationWithLumberjack_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:StartConverzationWithLumberjack.@method:eventsClass_StartConverzationWithLumberjack_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:StartConverzationWithLumberjack"
			method = "@method:eventsClass_StartConverzationWithLumberjack_Condition"
			raise MethodRuntimeException("An Exception in @class:StartConverzationWithLumberjack during method @method:eventsClass_StartConverzationWithLumberjack_Condition\n@class:StartConverzationWithLumberjack\n@method:eventsClass_StartConverzationWithLumberjack_Condition\n" + message)
	
	def _Condition(self):
		start_conversation = "Začít konverzaci"
		continue_conversation = "Pokračovat v konverzaci"
		chosen_option = objects.story.choosenOption
		is_start = chosen_option == start_conversation
		is_continue = chosen_option == continue_conversation
		return is_start or is_continue
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:StartConverzationWithLumberjack.@method:eventsClass_StartConverzationWithLumberjack_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:StartConverzationWithLumberjack"
			method = "@method:eventsClass_StartConverzationWithLumberjack_Action"
			raise MethodRuntimeException("An Exception in @class:StartConverzationWithLumberjack during method @method:eventsClass_StartConverzationWithLumberjack_Action\n@class:StartConverzationWithLumberjack\n@method:eventsClass_StartConverzationWithLumberjack_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		
		objects.story.lastMessage.options [0] = "Jít se podívat na louku"
		objects.story.lastMessage.options [1] = "Jít hledat k jezeru"
		index = 2
		
		if objects.Derek.isTypeOfItemInInventory(class_Apple):
			objects.story.lastMessage.options [index] = "Zkusit prodat nějaké jablko"
			index += 1
		
		objects.story.lastMessage.options [index] = "Zeptat se na ztracenou ovci"
		
		message = """
		"""
		objects.story.lastMessage.message = message
		pass
		pass


class event_QuestionLumberjackLostSheep:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_QuestionLumberjackLostSheep, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"TryToSellApplesToLumberjack",
		"FirstMeetFisherman",
		"AskLumberjackToGoToDeepForest",
		"LookingForSheepInDeepForest",
		"GoToMeadow",
		"LookingForSheepInDeepForestWithoutLumberjack",
	]
	
	def loadConditionMethod(self):
		script_path = './QuestionLumberjackLostSheep_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './QuestionLumberjackLostSheep_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:QuestionLumberjackLostSheep.@method:eventsClass_QuestionLumberjackLostSheep_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:QuestionLumberjackLostSheep"
			method = "@method:eventsClass_QuestionLumberjackLostSheep_Condition"
			raise MethodRuntimeException("An Exception in @class:QuestionLumberjackLostSheep during method @method:eventsClass_QuestionLumberjackLostSheep_Condition\n@class:QuestionLumberjackLostSheep\n@method:eventsClass_QuestionLumberjackLostSheep_Condition\n" + message)
	
	def _Condition(self):
		return "Zeptat se na ztracenou ovci" == objects.story.choosenOption
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:QuestionLumberjackLostSheep.@method:eventsClass_QuestionLumberjackLostSheep_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:QuestionLumberjackLostSheep"
			method = "@method:eventsClass_QuestionLumberjackLostSheep_Action"
			raise MethodRuntimeException("An Exception in @class:QuestionLumberjackLostSheep during method @method:eventsClass_QuestionLumberjackLostSheep_Action\n@class:QuestionLumberjackLostSheep\n@method:eventsClass_QuestionLumberjackLostSheep_Action\n" + message)
	
	def _Action(self):
		objects.Derek.addInformation("Ovce zaběhla do hlubokého lesa.")
		
		objects.story.lastMessage.clearOptionsArray()
		
		objects.story.lastMessage.options [0] = "Jít se podívat na louku"
		objects.story.lastMessage.options [1] = "Jít hledat k jezeru"
		objects.story.lastMessage.options [2] = "Jít hledat hluboko do lesa bez dřevorubce"
		objects.story.lastMessage.options [3] = "Poprosit dřevorubce, aby šel se mnou do hlubokého lesa"
		index = 4
		
		if objects.Derek.isTypeOfItemInInventory(class_Apple):
			objects.story.lastMessage.options [index] = "Zkusit prodat nějaké jablko"
			index += 1
		
		message = """
		
		"Neviděl jste, nebo neslyšel jste tady náhodou nedávno 
		ovci pastýře z louky? Bohužel se mu jedna někde ztratila
		a já mu jí nyní pomáhám nalézt."
		
		"Hmm..." Zamyslel se dřevorubec. "No jistě, viděl jsem nějakou ovci.
		Napadlo mě že se asi ztratila, tak jsem se ji snažil chytnout. 
		Bohužel byla moc vystrašená a rychlá. Utekla mi směrem 
		dál do hlubšího lesa. Bojím se, že jí už sežrali vlci."
		
		"Jestli se budeš chtít vydat do lesa tak pozor na vlky, nejednoho už
		sežrali."
		
		"""
		objects.story.lastMessage.message = message
		pass
		pass


class event_ComeBackFromDeepForest:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_ComeBackFromDeepForest, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"FirstMeetFisherman",
		"GoToMeadow",
	]
	
	def loadConditionMethod(self):
		script_path = './ComeBackFromDeepForest_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './ComeBackFromDeepForest_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:ComeBackFromDeepForest.@method:eventsClass_ComeBackFromDeepForest_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:ComeBackFromDeepForest"
			method = "@method:eventsClass_ComeBackFromDeepForest_Condition"
			raise MethodRuntimeException("An Exception in @class:ComeBackFromDeepForest during method @method:eventsClass_ComeBackFromDeepForest_Condition\n@class:ComeBackFromDeepForest\n@method:eventsClass_ComeBackFromDeepForest_Condition\n" + message)
	
	def _Condition(self):
		comeBack = "Vrátit se zpět" == objects.story.choosenOption
		comeBackWithoutLumberjack = "Vrátit se zpět bez dřevorubce" == objects.story.choosenOption
		return comeBack or comeBackWithoutLumberjack
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:ComeBackFromDeepForest.@method:eventsClass_ComeBackFromDeepForest_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:ComeBackFromDeepForest"
			method = "@method:eventsClass_ComeBackFromDeepForest_Action"
			raise MethodRuntimeException("An Exception in @class:ComeBackFromDeepForest during method @method:eventsClass_ComeBackFromDeepForest_Action\n@class:ComeBackFromDeepForest\n@method:eventsClass_ComeBackFromDeepForest_Action\n" + message)
	
	def _Action(self):
		last_location = objects.Derek.location
		comeBackWithoutLumberjack = "Vrátit se zpět bez dřevorubce" == objects.story.choosenOption
		objects.Derek.location = objects.forest_leafy
		if not comeBackWithoutLumberjack:
			objects.lumberjack.location = objects.forest_leafy
			message  = """
		"Tak bouhžel, ale rád jsem ti pomohl. Můžeš se zkusit poptat někde jinde."
			"""
		else:
			message  = """
		Derek doběhl až k místu, kde dřevorubec dříve pracoval. Opřel se o strom 
		a z hluboka dýchal. Ohlédl se, jestli neuslyší, nebo neuvidí vlky... Nikde nikdo,
		oddechl si Derek.
			"""
		
		
		objects.story.lastMessage.clearOptionsArray()
		
		objects.story.lastMessage.options [0] = "Jít hledat k jezeru"
		objects.story.lastMessage.options [1] = "Jít se podívat na louku"
		objects.story.lastMessage.message = message
		
		pass
		pass


class event_GoToCliff:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_GoToCliff, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"RetrunSheepToMeadow",
	]
	
	def loadConditionMethod(self):
		script_path = './GoToCliff_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './GoToCliff_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:GoToCliff.@method:eventsClass_GoToCliff_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:GoToCliff"
			method = "@method:eventsClass_GoToCliff_Condition"
			raise MethodRuntimeException("An Exception in @class:GoToCliff during method @method:eventsClass_GoToCliff_Condition\n@class:GoToCliff\n@method:eventsClass_GoToCliff_Condition\n" + message)
	
	def _Condition(self):
		return  "Jít se podívat k útesu" == objects.story.choosenOption
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:GoToCliff.@method:eventsClass_GoToCliff_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:GoToCliff"
			method = "@method:eventsClass_GoToCliff_Action"
			raise MethodRuntimeException("An Exception in @class:GoToCliff during method @method:eventsClass_GoToCliff_Action\n@class:GoToCliff\n@method:eventsClass_GoToCliff_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		objects.story.lastMessage.options [0] = "Vrátit se zpět na louku za pastevcem"
	
		objects.Derek.location = objects.cliff
		message = """
		Po travnaté louce Derek lezl do kopce směrem k hraně 
		skalní stěny. Ovce zatím nikde. Zadýchaný dál
		pokračoval až skoro nahoru, když náhle uslyšel slabé 
		zabečení. Rozhlédl se... a uviděl bílé cosi vykukující z trávy.
		Ovečka si hověla v trávě a přežvikovala.
		"""
		
		objects.Derek.addInformation("Ovce šla směrem k útesu")
		objects.story.lastMessage.message = message
		pass
		pass


class event_FirstMeetFisherman:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_FirstMeetFisherman, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"GoToMeadow",
		"FirstMeetLumberjack",
		"StartConversationWithFisherman",
	]
	
	def loadConditionMethod(self):
		script_path = './FirstMeetFisherman_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './FirstMeetFisherman_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:FirstMeetFisherman.@method:eventsClass_FirstMeetFisherman_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:FirstMeetFisherman"
			method = "@method:eventsClass_FirstMeetFisherman_Condition"
			raise MethodRuntimeException("An Exception in @class:FirstMeetFisherman during method @method:eventsClass_FirstMeetFisherman_Condition\n@class:FirstMeetFisherman\n@method:eventsClass_FirstMeetFisherman_Condition\n" + message)
	
	def _Condition(self):
		return objects.story.choosenOption == "Jít hledat k jezeru"
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:FirstMeetFisherman.@method:eventsClass_FirstMeetFisherman_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:FirstMeetFisherman"
			method = "@method:eventsClass_FirstMeetFisherman_Action"
			raise MethodRuntimeException("An Exception in @class:FirstMeetFisherman during method @method:eventsClass_FirstMeetFisherman_Action\n@class:FirstMeetFisherman\n@method:eventsClass_FirstMeetFisherman_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		
		objects.story.lastMessage.options [0] = "Jít se podívat na louku"
		objects.story.lastMessage.options [1] = "Zahájit konverzaci"
		
		index = 2
		info = objects.Derek.informations
		if not "Dřevorubce sežrali vlci" in info and not "Ovce není hluboko v lese"  in info:
			objects.story.lastMessage.options [index] = "Jít hledat do lesa"
			index += 1
		objects.Derek.location = objects.lake
		
		objects.story.lastMessage.message = """
		Derek přichází k nevelikému jezírku poblíž louky a lesa.  Na modré
		hladině se leskne slunneční třpyt. A cvrčci pod nohama odskakují do
		vedlejší trávy. 
		
		Na břehu si hoví rybář s udicí ve vodě a ve stínu vrby podřimuje.
		"""
		pass
		pass


class event_ExchangeInfoForCopperCoin:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_ExchangeInfoForCopperCoin, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"GoToMeadow",
	]
	
	def loadConditionMethod(self):
		script_path = './ExchangeInfoForCopperCoin_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './ExchangeInfoForCopperCoin_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:ExchangeInfoForCopperCoin.@method:eventsClass_ExchangeInfoForCopperCoin_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:ExchangeInfoForCopperCoin"
			method = "@method:eventsClass_ExchangeInfoForCopperCoin_Condition"
			raise MethodRuntimeException("An Exception in @class:ExchangeInfoForCopperCoin during method @method:eventsClass_ExchangeInfoForCopperCoin_Condition\n@class:ExchangeInfoForCopperCoin\n@method:eventsClass_ExchangeInfoForCopperCoin_Condition\n" + message)
	
	def _Condition(self):
		return  "Zkusit vyměnit informaci za měděnou minci" == objects.story.choosenOption
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:ExchangeInfoForCopperCoin.@method:eventsClass_ExchangeInfoForCopperCoin_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:ExchangeInfoForCopperCoin"
			method = "@method:eventsClass_ExchangeInfoForCopperCoin_Action"
			raise MethodRuntimeException("An Exception in @class:ExchangeInfoForCopperCoin during method @method:eventsClass_ExchangeInfoForCopperCoin_Action\n@class:ExchangeInfoForCopperCoin\n@method:eventsClass_ExchangeInfoForCopperCoin_Action\n" + message)
	
	def _Action(self):
		objects.Derek.location = objects.lake
		objects.story.lastMessage.clearOptionsArray()
		
		objects.story.lastMessage.options [0] = "Jít se podívat na louku"
		index = 1
		info = objects.Derek.informations
		if not "Dřevorubce sežrali vlci" in info and not "Ovce není hluboko v lese"  in info:
			objects.story.lastMessage.options [index] = "Jít hledat do lesa"
			index += 1
		
		message = """
		"Co byste řekl na to, že vám za tu informaci dám tuto minci?"
		"Hmm... Tuto říkáte? Asi ne díky."
		"""
		objects.story.lastMessage.message = message
		pass
		pass


class event_RetrunSheepToMeadow:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_RetrunSheepToMeadow, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
	]
	
	def loadConditionMethod(self):
		script_path = './RetrunSheepToMeadow_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './RetrunSheepToMeadow_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:RetrunSheepToMeadow.@method:eventsClass_RetrunSheepToMeadow_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:RetrunSheepToMeadow"
			method = "@method:eventsClass_RetrunSheepToMeadow_Condition"
			raise MethodRuntimeException("An Exception in @class:RetrunSheepToMeadow during method @method:eventsClass_RetrunSheepToMeadow_Condition\n@class:RetrunSheepToMeadow\n@method:eventsClass_RetrunSheepToMeadow_Condition\n" + message)
	
	def _Condition(self):
		return "Vrátit se zpět na louku za pastevcem" == objects.story.choosenOption
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:RetrunSheepToMeadow.@method:eventsClass_RetrunSheepToMeadow_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:RetrunSheepToMeadow"
			method = "@method:eventsClass_RetrunSheepToMeadow_Action"
			raise MethodRuntimeException("An Exception in @class:RetrunSheepToMeadow during method @method:eventsClass_RetrunSheepToMeadow_Action\n@class:RetrunSheepToMeadow\n@method:eventsClass_RetrunSheepToMeadow_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		
		objects.Derek.location = objects.meadow
		objects.lostSheep.location = objects.meadow
		
		message = """
		Derek se snažil nahánět ovečku správným směrem zpět 
		na louku za pastevcem ke stádu.
		
		Z dálky bylo z výšky stádo vidět jako kupa malých bílích teček.
		Nakonec se ale Derekovi povedlo nahnat ovci zprátky.
		
		Pastevec byl velmi nadšený a poděkoval Derekovi za jeho službu.
		
		Derek pak následně pokračoval v cestování dál.
		
		
		Konec
		"""
		
		objects.story.lastMessage.message = message
		pass
		pass


class event_AskLumberjackToGoToDeepForest:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_AskLumberjackToGoToDeepForest, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"LookingForSheepInDeepForest",
	]
	
	def loadConditionMethod(self):
		script_path = './AskLumberjackToGoToDeepForest_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './AskLumberjackToGoToDeepForest_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:AskLumberjackToGoToDeepForest.@method:eventsClass_AskLumberjackToGoToDeepForest_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:AskLumberjackToGoToDeepForest"
			method = "@method:eventsClass_AskLumberjackToGoToDeepForest_Condition"
			raise MethodRuntimeException("An Exception in @class:AskLumberjackToGoToDeepForest during method @method:eventsClass_AskLumberjackToGoToDeepForest_Condition\n@class:AskLumberjackToGoToDeepForest\n@method:eventsClass_AskLumberjackToGoToDeepForest_Condition\n" + message)
	
	def _Condition(self):
		return "Poprosit dřevorubce, aby šel se mnou do hlubokého lesa" == objects.story.choosenOption
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:AskLumberjackToGoToDeepForest.@method:eventsClass_AskLumberjackToGoToDeepForest_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:AskLumberjackToGoToDeepForest"
			method = "@method:eventsClass_AskLumberjackToGoToDeepForest_Action"
			raise MethodRuntimeException("An Exception in @class:AskLumberjackToGoToDeepForest during method @method:eventsClass_AskLumberjackToGoToDeepForest_Action\n@class:AskLumberjackToGoToDeepForest\n@method:eventsClass_AskLumberjackToGoToDeepForest_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		objects.story.lastMessage.options [0] = "Jít hledat hluboko do lesa"
		message = """
		"Nemohl byste tam jít prosím se mnou? Já se vlků
		dost bojím."
		
		"Hmm..." Zamyslel se dřevorubec. "Nu dobrá.
		Pastevec je můj přítel a když je to jeho ovce, proč ne.
		Alespoň si dám chvíli pauzu."
		"""
		objects.story.lastMessage.message = message
		pass
		pass


class event_StartConversationWithShepherd:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_StartConversationWithShepherd, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"FirstMeetFisherman",
		"FirstMeetLumberjack",
	]
	
	def loadConditionMethod(self):
		script_path = './StartConversationWithShepherd_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './StartConversationWithShepherd_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:StartConversationWithShepherd.@method:eventsClass_StartConversationWithShepherd_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:StartConversationWithShepherd"
			method = "@method:eventsClass_StartConversationWithShepherd_Condition"
			raise MethodRuntimeException("An Exception in @class:StartConversationWithShepherd during method @method:eventsClass_StartConversationWithShepherd_Condition\n@class:StartConversationWithShepherd\n@method:eventsClass_StartConversationWithShepherd_Condition\n" + message)
	
	def _Condition(self):
		return objects.story.choosenOption == "Začít konverzaci"
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:StartConversationWithShepherd.@method:eventsClass_StartConversationWithShepherd_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:StartConversationWithShepherd"
			method = "@method:eventsClass_StartConversationWithShepherd_Action"
			raise MethodRuntimeException("An Exception in @class:StartConversationWithShepherd during method @method:eventsClass_StartConversationWithShepherd_Action\n@class:StartConversationWithShepherd\n@method:eventsClass_StartConversationWithShepherd_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		
		objects.story.lastMessage.options [0] = "Jít hledat k jezeru"
		index = 1
		info = objects.Derek.informations
		if not "Dřevorubce sežrali vlci" in info and not "Ovce není hluboko v lese"  in info:
			objects.story.lastMessage.options [index] = "Jít hledat do lesa"
			index += 1
		
		objects.story.lastMessage.message  = """
		"Co bych tak řekl, moc toho není. Utekla asi buď k lesu, nebo k jezeru.
		Zeptal bych se lidí jestli ji neviděli. U jezera bývá rybář, v lese naopak
		bývá lesník." Vyjádřil se pastevec.
		"""
		pass
		pass


class event_LookingForSheepInDeepForest:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_LookingForSheepInDeepForest, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"ComeBackFromDeepForest",
	]
	
	def loadConditionMethod(self):
		script_path = './LookingForSheepInDeepForest_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './LookingForSheepInDeepForest_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:LookingForSheepInDeepForest.@method:eventsClass_LookingForSheepInDeepForest_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:LookingForSheepInDeepForest"
			method = "@method:eventsClass_LookingForSheepInDeepForest_Condition"
			raise MethodRuntimeException("An Exception in @class:LookingForSheepInDeepForest during method @method:eventsClass_LookingForSheepInDeepForest_Condition\n@class:LookingForSheepInDeepForest\n@method:eventsClass_LookingForSheepInDeepForest_Condition\n" + message)
	
	def _Condition(self):
		return "Jít hledat hluboko do lesa" == objects.story.choosenOption
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:LookingForSheepInDeepForest.@method:eventsClass_LookingForSheepInDeepForest_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:LookingForSheepInDeepForest"
			method = "@method:eventsClass_LookingForSheepInDeepForest_Action"
			raise MethodRuntimeException("An Exception in @class:LookingForSheepInDeepForest during method @method:eventsClass_LookingForSheepInDeepForest_Action\n@class:LookingForSheepInDeepForest\n@method:eventsClass_LookingForSheepInDeepForest_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		message = """
		Derek kráčí hlouběji a hlouběji do lesa. Jehličnaté stromy
		se tyčí vysoko do nebe, které je zakryté hustou vrstvou 
		jehličí...
		
		Po ovci ovšem ani vidu, ani slechu... Nikde nic. Nebo že by? Něco se 
		zlatavě zalesklo na jehličí. Opravdu, na jehličí ležela zlatá mince.
		"Jak se tady obějvila?" Podivil se Derek...
		
		Když tu náhle hluboké hrdelní "Wrarwrr...".
		Derekovi ztuhla krev v žilách. Z několika stran se objevila
		smečka několika vlků a pomalu  se přibližovala zpoza stromů.
		
		"""
		
		objects.Derek.addItemToInventory(objects.goldenCoin)
		
		message += """
		Dřevorubec se postavil ochranářsky před Dereka dřímající 
		sekyru pevně v rukou.
		
		Vlci se ale nepřestali přibližovat. Oba pomalu couvali, 
		když tu první vlk zaútočil. 
		
		Švih sem, švih tam, sekyra se míhala vzduchem...
		První vlk zakňučel, když se ostří zarylo do huňatého kožichu.
		Další vlk zaútočil, ale záhy uskočil před dřevorubcovým nápřahem.
		Dřevorubec hlasitě oddechoval.
		"""
		
		if "Mám hlad" in objects.lumberjack.informations:
			message += """
			"Mám docela hlad." Sýpavím hlasem zašeptal dřevorubec...
			Dřevorubec se vzepřel a odrazil dalších několik vlčích výpadů.
			První vlk se svalil bezvládně na zem a sekyra se zabarvila
			kapičkami krve.
			
			Švih, mách, Derek pozorně sledoval souboj. Jeden z vlků se
			zahryzl muži hluboko do ruky. Dřevorubec zavyl bolestí a 
			podlomila se mu kolena.
			
			Derek ucítil, že souboj asi nedopadne dobře. Rozeběhl se rychle pryč.
			"""
			objects.Derek.addInformation("Dřevorubce sežrali vlci")
			objects.story.lastMessage.options [0] = "Vrátit se zpět bez dřevorubce"
		else:
			message += """
			Dřevorubec se vzepřel a odrazil dalších několik vlčích výpadů.
			První vlk se svalil bezvládně na zem a sekyra se zabarvila
			kapičkami krve. "Ještě že jsem si dal  něco k snědku." Utrousil
			pod vousy.
			
			Švih, mách, Derek pozorně sledoval souboj. Dřevorubec zasadil
			další tvrdou ránu a další vlk se zapotácel a odběhl pryč do lesa.
			
			Podobně jeho soudruh pocítil, že nemá šanci a zmizel v hustém porostu.
			Dřevorubec zhluboka oddechoval.
			
			
			Derek pokračoval v prohledání lesa. Nikde nic. 
			
			Vrátil se zpět za dřevorubcem. "Po  ovci nikde ani památky.
			Asi ji sežraly ti vlci." Dřevorubec se zamyslel. "Hmm... Ale to by 
			jsi našel ostatky."
			"""
			objects.Derek.addInformation("Ovce není hluboko v lese")
			objects.story.lastMessage.options [0] = "Vrátit se zpět"
		objects.story.lastMessage.message = message
		
		objects.lumberjack.location = objects.deep_forest
		objects.Derek.location = objects.deep_forest
		
		
		pass
		pass


class event_ExchangeInfoForGoldenCoin:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_ExchangeInfoForGoldenCoin, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"GoToCliff",
	]
	
	def loadConditionMethod(self):
		script_path = './ExchangeInfoForGoldenCoin_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './ExchangeInfoForGoldenCoin_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:ExchangeInfoForGoldenCoin.@method:eventsClass_ExchangeInfoForGoldenCoin_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:ExchangeInfoForGoldenCoin"
			method = "@method:eventsClass_ExchangeInfoForGoldenCoin_Condition"
			raise MethodRuntimeException("An Exception in @class:ExchangeInfoForGoldenCoin during method @method:eventsClass_ExchangeInfoForGoldenCoin_Condition\n@class:ExchangeInfoForGoldenCoin\n@method:eventsClass_ExchangeInfoForGoldenCoin_Condition\n" + message)
	
	def _Condition(self):
		return  "Zkusit vyměnit informaci za zlatou minci" == objects.story.choosenOption
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:ExchangeInfoForGoldenCoin.@method:eventsClass_ExchangeInfoForGoldenCoin_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:ExchangeInfoForGoldenCoin"
			method = "@method:eventsClass_ExchangeInfoForGoldenCoin_Action"
			raise MethodRuntimeException("An Exception in @class:ExchangeInfoForGoldenCoin during method @method:eventsClass_ExchangeInfoForGoldenCoin_Action\n@class:ExchangeInfoForGoldenCoin\n@method:eventsClass_ExchangeInfoForGoldenCoin_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		objects.Derek.addInformation("Ovce šla směrem k útesu")
		
		objects.story.lastMessage.options [0] = "Jít se podívat k útesu"
		
		message = """
		"Co byste řekl na to, že vám za tu informaci dám tuto zlatou minci?"
		"Hmm... Tuto říkáte? Tak to se můžeme domluvit."
		
		Derek předal rybáři nalezenou zlatou minci  a rybář odpověděl.
		"Dobrá tedy. Ovce vyběhla z lesa, což je docela zvláštní, nicméně pak se vydala
		směrem nahoru po louce kolem lesa. Směřovala k útesu."'
		"""
		objects.story.lastMessage.message = message
		
		pass
		pass


class event_GoToMeadow:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_GoToMeadow, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"FirstMeetFisherman",
		"StartConversationWithShepherd",
		"FirstMeetLumberjack",
	]
	
	def loadConditionMethod(self):
		script_path = './GoToMeadow_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './GoToMeadow_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:GoToMeadow.@method:eventsClass_GoToMeadow_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:GoToMeadow"
			method = "@method:eventsClass_GoToMeadow_Condition"
			raise MethodRuntimeException("An Exception in @class:GoToMeadow during method @method:eventsClass_GoToMeadow_Condition\n@class:GoToMeadow\n@method:eventsClass_GoToMeadow_Condition\n" + message)
	
	def _Condition(self):
		return objects.story.choosenOption == "Jít se podívat na louku"
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:GoToMeadow.@method:eventsClass_GoToMeadow_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:GoToMeadow"
			method = "@method:eventsClass_GoToMeadow_Action"
			raise MethodRuntimeException("An Exception in @class:GoToMeadow during method @method:eventsClass_GoToMeadow_Action\n@class:GoToMeadow\n@method:eventsClass_GoToMeadow_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		objects.Derek.location = objects.meadow
		
		objects.story.lastMessage.options [0] = "Začít konverzaci"
		objects.story.lastMessage.options [1] = "Jít hledat k jezeru"
		info = objects.Derek.informations
		if not "Dřevorubce sežrali vlci" in info and not "Ovce není hluboko v lese"  in info:
			objects.story.lastMessage.options [2] = "Jít hledat do lesa"
		
		message = """
		Derek přichází zpět na louku ke stádu ovcí s pastevcem.
		Přivítá ho štěkot a kývnutí na pozdrav.
		"""
		objects.story.lastMessage.message = message
		pass
		pass


class event_FirstMeetLumberjack:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_FirstMeetLumberjack, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"StartConverzationWithLumberjack",
		"FirstMeetFisherman",
		"GoToMeadow",
	]
	
	def loadConditionMethod(self):
		script_path = './FirstMeetLumberjack_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './FirstMeetLumberjack_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:FirstMeetLumberjack.@method:eventsClass_FirstMeetLumberjack_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:FirstMeetLumberjack"
			method = "@method:eventsClass_FirstMeetLumberjack_Condition"
			raise MethodRuntimeException("An Exception in @class:FirstMeetLumberjack during method @method:eventsClass_FirstMeetLumberjack_Condition\n@class:FirstMeetLumberjack\n@method:eventsClass_FirstMeetLumberjack_Condition\n" + message)
	
	def _Condition(self):
		return objects.story.choosenOption == "Jít hledat do lesa"
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:FirstMeetLumberjack.@method:eventsClass_FirstMeetLumberjack_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:FirstMeetLumberjack"
			method = "@method:eventsClass_FirstMeetLumberjack_Action"
			raise MethodRuntimeException("An Exception in @class:FirstMeetLumberjack during method @method:eventsClass_FirstMeetLumberjack_Action\n@class:FirstMeetLumberjack\n@method:eventsClass_FirstMeetLumberjack_Action\n" + message)
	
	def _Action(self):
		last_location = objects.Derek.location
		objects.Derek.location = objects.forest_leafy
		message = ""
		if last_location == objects.meadow:
			message  = """
		Derek kráčí po okousané travnaté pastvnině k 
		vysokým prostupným jehličnatým stromům. 
			"""
		elif last_location == objects.lake:
			message = """
		Derek se vrací zpět zkrz hustý porost do stínu 
		vysokých stromů. 
			"""
		message += """
		Prochází mezi vysokými šedivými kmeny s
		tmavým jehličým zakrývající oblohu. Rozhlíží 
		se a naslouchá, ovšem po ztracené ovci ani 
		památky. Pouze cvrlikot ptáčků, line se krajinou...
		
		Když tu, slabé klepání ozývá se z dáli. S každým 
		krokem zvuk pracovité sekery sílí. 
		"Dobrý" pozdravil Derek dřevorubce.
		"Dobrej" odpověděl nepřítomně statný chlapík 
		a otřel si orosené čelo do rukávu.
		"""
		objects.story.lastMessage.clearOptionsArray()
		
		objects.story.lastMessage.options [0] = "Začít konverzaci"
		objects.story.lastMessage.options [1] = "Jít hledat k jezeru"
		objects.story.lastMessage.options [2] = "Jít se podívat na louku"
		
		objects.story.lastMessage.message = message
		pass
		pass


class event_StartConversationWithFisherman:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_StartConversationWithFisherman, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"GoToMeadow",
		"FirstMeetLumberjack",
		"QuestionFishermanLostSheep",
	]
	
	def loadConditionMethod(self):
		script_path = './StartConversationWithFisherman_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './StartConversationWithFisherman_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:StartConversationWithFisherman.@method:eventsClass_StartConversationWithFisherman_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:StartConversationWithFisherman"
			method = "@method:eventsClass_StartConversationWithFisherman_Condition"
			raise MethodRuntimeException("An Exception in @class:StartConversationWithFisherman during method @method:eventsClass_StartConversationWithFisherman_Condition\n@class:StartConversationWithFisherman\n@method:eventsClass_StartConversationWithFisherman_Condition\n" + message)
	
	def _Condition(self):
		return objects.story.choosenOption == "Zahájit konverzaci"
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:StartConversationWithFisherman.@method:eventsClass_StartConversationWithFisherman_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:StartConversationWithFisherman"
			method = "@method:eventsClass_StartConversationWithFisherman_Action"
			raise MethodRuntimeException("An Exception in @class:StartConversationWithFisherman during method @method:eventsClass_StartConversationWithFisherman_Action\n@class:StartConversationWithFisherman\n@method:eventsClass_StartConversationWithFisherman_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		
		objects.story.lastMessage.options [0] = "Jít se podívat na louku"
		objects.story.lastMessage.options [1] = "Zeptat se na ovci"
		index = 2
		info = objects.Derek.informations
		if not "Dřevorubce sežrali vlci" in info and not "Ovce není hluboko v lese"  in info:
			objects.story.lastMessage.options [index] = "Jít hledat do lesa"
			index += 1
		
		message = """
		Derek přišel blíže. "Rád bych se na něco zeptal..."
		"""
		objects.story.lastMessage.message = message
		pass
		pass


class event_QuestionFishermanLostSheep:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_QuestionFishermanLostSheep, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"ExchangeInfoForCopperCoin",
		"ExchangeInfoForGoldenCoin",
		"GoToMeadow",
		"FirstMeetLumberjack",
	]
	
	def loadConditionMethod(self):
		script_path = './QuestionFishermanLostSheep_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './QuestionFishermanLostSheep_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:QuestionFishermanLostSheep.@method:eventsClass_QuestionFishermanLostSheep_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:QuestionFishermanLostSheep"
			method = "@method:eventsClass_QuestionFishermanLostSheep_Condition"
			raise MethodRuntimeException("An Exception in @class:QuestionFishermanLostSheep during method @method:eventsClass_QuestionFishermanLostSheep_Condition\n@class:QuestionFishermanLostSheep\n@method:eventsClass_QuestionFishermanLostSheep_Condition\n" + message)
	
	def _Condition(self):
		return objects.story.choosenOption == "Zeptat se na ovci"
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:QuestionFishermanLostSheep.@method:eventsClass_QuestionFishermanLostSheep_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:QuestionFishermanLostSheep"
			method = "@method:eventsClass_QuestionFishermanLostSheep_Action"
			raise MethodRuntimeException("An Exception in @class:QuestionFishermanLostSheep during method @method:eventsClass_QuestionFishermanLostSheep_Action\n@class:QuestionFishermanLostSheep\n@method:eventsClass_QuestionFishermanLostSheep_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		
		objects.story.lastMessage.options [0] = "Jít se podívat na louku"
		index = 1
		info = objects.Derek.informations
		if not "Dřevorubce sežrali vlci" in info and not "Ovce není hluboko v lese"  in info:
			objects.story.lastMessage.options [index] = "Jít hledat do lesa"
			index += 1
		
		if objects.goldenCoin in objects.Derek.inventory:
			objects.story.lastMessage.options [index] = "Zkusit vyměnit informaci za zlatou minci"
			index += 1
		if objects.copperCoin in objects.Derek.inventory:
			objects.story.lastMessage.options [index] = "Zkusit vyměnit informaci za měděnou minci"
			index += 1
		
		message = """
		"Hledám ovci, neviděl jste ji náhodou?"
		Rybář si rozespale promnul oči a odpověděl.
		"Náhodou viděl. Byla tady a někam odešla."
		
		Derek chvíli čekal, ale když viděl že rybář nehodlá pokračovat,
		zeptal se. "A nevíte náhodou kam odešla? Víte, ta ovce patří zdejšímu
		pastevci." Rybář odpověděl. "Ano, vím jakým směrem odešla, ale
		nezajímá mě to. Pastevec se ke mně nechová zrovna hezky a 
		tak nemám potřebu mu pomáhat."
		"""
		objects.story.lastMessage.message = message
		pass
		pass


class event_LookingForSheepInDeepForestWithoutLumberjack:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_LookingForSheepInDeepForestWithoutLumberjack, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
	]
	
	def loadConditionMethod(self):
		script_path = './LookingForSheepInDeepForestWithoutLumberjack_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './LookingForSheepInDeepForestWithoutLumberjack_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:LookingForSheepInDeepForestWithoutLumberjack.@method:eventsClass_LookingForSheepInDeepForestWithoutLumberjack_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:LookingForSheepInDeepForestWithoutLumberjack"
			method = "@method:eventsClass_LookingForSheepInDeepForestWithoutLumberjack_Condition"
			raise MethodRuntimeException("An Exception in @class:LookingForSheepInDeepForestWithoutLumberjack during method @method:eventsClass_LookingForSheepInDeepForestWithoutLumberjack_Condition\n@class:LookingForSheepInDeepForestWithoutLumberjack\n@method:eventsClass_LookingForSheepInDeepForestWithoutLumberjack_Condition\n" + message)
	
	def _Condition(self):
		return "Jít hledat hluboko do lesa bez dřevorubce" == objects.story.choosenOption
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:LookingForSheepInDeepForestWithoutLumberjack.@method:eventsClass_LookingForSheepInDeepForestWithoutLumberjack_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:LookingForSheepInDeepForestWithoutLumberjack"
			method = "@method:eventsClass_LookingForSheepInDeepForestWithoutLumberjack_Action"
			raise MethodRuntimeException("An Exception in @class:LookingForSheepInDeepForestWithoutLumberjack during method @method:eventsClass_LookingForSheepInDeepForestWithoutLumberjack_Action\n@class:LookingForSheepInDeepForestWithoutLumberjack\n@method:eventsClass_LookingForSheepInDeepForestWithoutLumberjack_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		message = """
		Derek kráčí hlouběji a hlouběji do lesa. Jehličnaté stromy
		se tyčí vysoko do nebe, které je zakryté hustou vrstvou 
		jehličí...
		
		Po ovci ovšem ani vidu, ani slechu... Nikde nic. Nebo že by? Něco se 
		zlatavě zalesklo na jehličí. Opravdu, na jehličí ležela zlatá mince.
		"Jak se tady obějvila?" Podivil se Derek...
		
		Když tu náhle hluboké hrdelní "Wrarwrr...".
		Derekovi ztuhla krev v žilách. Z několika stran se objevila
		smečka několika vlků a pomalu  se přibližovala zpoza stromů.
		
		Derek utíkal. Utíkal co mu nohy stačily, zároveň ale za sebou slyšel dusot
		vlčích tlapek. Srdce mu bilo, rychle pryč... "Á" Derek zakopnul o kořen, ale
		adrenalin ho rychle vyšvihl zpět na nohy. Pravá, levá...
		
		Nohou mu projela silná bolest, jak se mu něco zahryzlo do stehna...
		Následoval křik a pád.
		
		
		Konec
		"""
		objects.Derek.location = objects.deep_forest
		objects.story.lastMessage.message = message
		pass
		pass


	
class Events:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(Events, cls).__new__(cls)
		return cls._instance
	TryToSellApplesToLumberjack = event_TryToSellApplesToLumberjack()
	Initialize = event_Initialize()
	StartConverzationWithLumberjack = event_StartConverzationWithLumberjack()
	QuestionLumberjackLostSheep = event_QuestionLumberjackLostSheep()
	ComeBackFromDeepForest = event_ComeBackFromDeepForest()
	GoToCliff = event_GoToCliff()
	FirstMeetFisherman = event_FirstMeetFisherman()
	ExchangeInfoForCopperCoin = event_ExchangeInfoForCopperCoin()
	RetrunSheepToMeadow = event_RetrunSheepToMeadow()
	AskLumberjackToGoToDeepForest = event_AskLumberjackToGoToDeepForest()
	StartConversationWithShepherd = event_StartConversationWithShepherd()
	LookingForSheepInDeepForest = event_LookingForSheepInDeepForest()
	ExchangeInfoForGoldenCoin = event_ExchangeInfoForGoldenCoin()
	GoToMeadow = event_GoToMeadow()
	FirstMeetLumberjack = event_FirstMeetLumberjack()
	StartConversationWithFisherman = event_StartConversationWithFisherman()
	QuestionFishermanLostSheep = event_QuestionFishermanLostSheep()
	LookingForSheepInDeepForestWithoutLumberjack = event_LookingForSheepInDeepForestWithoutLumberjack()
	pass

events = Events()

class event_start:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_start, cls).__new__(cls)
		return cls._instance
	conditionMethodLoaded = True
	actionMethodLoaded = True
	sequence_next_events = [
				"Initialize",
	]

class EventTreeHead:
	last_event = event_start()
	def try_move(self):
		logging.info('Try to execute next event...')			
		for next_event_name in self.last_event.sequence_next_events:
			next_event = events.__getattribute__(next_event_name)
			if next_event.conditionMethodLoaded:
				try:
					if next_event.Condition():
						self.last_event = next_event
						logging.info('Executing next event: ' + next_event_name)
						self.last_event.Action()
						if event_tree_condition == EventGraphCondition.MOVE_UNTIL_POSSIBLE:
							self.try_move()
				except MethodRuntimeException as e:
					logging.error('MethodRuntimeException: ' + next_event_name + ' ' + e.message)
					print("_exception_")
					print(e.message)
					print("_exception_end_")

event_tree_head = EventTreeHead()

# ------------------- EVENTS TREE -------------------

from enum import Enum

class EventGraphCondition(Enum):
	MOVE_MAX_BY_ONE = 1
	MOVE_UNTIL_POSSIBLE = 2

event_tree_condition = EventGraphCondition.MOVE_MAX_BY_ONE