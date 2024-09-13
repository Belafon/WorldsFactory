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
		self.inventory = ArrayObject([10], "inventory", "@basicType:String")
		
	properties = {
		"__object_name__": "@basicType:String",
		"location": "@class:Place",
		"inventory": "@basicType:String"
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


class class_Player(class_Creature):
	def __init__(self, id):
		self._id = id
		self.inventory = ArrayObject([10], "inventory", "@basicType:String")
		
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
	}
	def __str__(self):
		return self._id
	
	pass



# ------------------- LIBRARY OBJECTS -------------------

class Objects:
	_instance = None
	
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(Objects, cls).__new__(cls)		
		cls.Jakub = class_Creature("@object:Jakub")
		cls.Jakub.__object_name__ = "Jakub"
		cls.Jakub.inventory.set_objects_name("Jakub")
		cls.mountain_meadow = class_Place("@object:mountain_meadow")
		cls.mountain_meadow.__object_name__ = "mountain_meadow"
		cls.forest_leafy = class_Place("@object:forest_leafy")
		cls.forest_leafy.__object_name__ = "forest_leafy"
		cls.Derek = class_Player("@object:Derek")
		cls.Derek.__object_name__ = "Derek"
		cls.Derek.inventory.set_objects_name("Derek")
		cls.cliff = class_Place("@object:cliff")
		cls.cliff.__object_name__ = "cliff"
		cls.currentStoryMessage = class_StoryMessage("@object:currentStoryMessage")
		cls.currentStoryMessage.__object_name__ = "currentStoryMessage"
		cls.currentStoryMessage.options.set_objects_name("currentStoryMessage")
		cls.time = class_Time("@object:time")
		cls.time.__object_name__ = "time"
		cls.example = class_Story("@object:example")
		cls.example.__object_name__ = "example"
		cls.story = class_Story("@object:story")
		cls.story.__object_name__ = "story"
		cls.west_forest_leafy = class_Place("@object:west_forest_leafy")
		cls.west_forest_leafy.__object_name__ = "west_forest_leafy"
		cls.map = class_Map("@object:map")
		cls.map.__object_name__ = "map"
		cls.map.places.set_objects_name("map")
		cls.meadow = class_Place("@object:meadow")
		cls.meadow.__object_name__ = "meadow"
		cls.deep_forest = class_Place("@object:deep_forest")
		cls.deep_forest.__object_name__ = "deep_forest"
		try:
			cls.Jakub_initialize(cls.Jakub)
			cls.mountain_meadow_initialize(cls.mountain_meadow)
			cls.forest_leafy_initialize(cls.forest_leafy)
			cls.Derek_initialize(cls.Derek)
			cls.cliff_initialize(cls.cliff)
			cls.currentStoryMessage_initialize(cls.currentStoryMessage)
			cls.time_initialize(cls.time)
			cls.example_initialize(cls.example)
			cls.story_initialize(cls.story)
			cls.west_forest_leafy_initialize(cls.west_forest_leafy)
			cls.map_initialize(cls.map)
			cls.meadow_initialize(cls.meadow)
			cls.deep_forest_initialize(cls.deep_forest)
		except Exception as e:
			print("_exception_")
			print("Error in python code, while initializing objects:" + str(e))
			print("_exception_end_")
			raise MethodRuntimeException("An Exception in an init method")
		return cls._instance
		pass
	
	def Jakub_initialize(Jakub):
		pass
	
	def mountain_meadow_initialize(mountain_meadow):
		mountain_meadow.description = "mountain_meadow"
		pass
	
	def forest_leafy_initialize(forest_leafy):
		forest_leafy.description = "forest_leafy"
		pass
	
	def Derek_initialize(Derek):
		pass
	
	def cliff_initialize(cliff):
		cliff.description = "cliff"
		cliff.weather = "idle"
		cliff.clouds = "overcast"
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
	
	def story_initialize(story):
		story.choosenOption = "startOption"
		pass
	
	def west_forest_leafy_initialize(west_forest_leafy):
		west_forest_leafy.description = "forest_leafy"
		pass
	
	def map_initialize(map):
		pass
	
	def meadow_initialize(meadow):
		meadow.description = "meadow"
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


class event_StartConversation:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_StartConversation, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"QuestionAboutWay",
		"ContinueInTravel",
	]
	
	def loadConditionMethod(self):
		script_path = './StartConversation_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './StartConversation_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:StartConversation.@method:eventsClass_StartConversation_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:StartConversation"
			method = "@method:eventsClass_StartConversation_Condition"
			raise MethodRuntimeException("An Exception in @class:StartConversation during method @method:eventsClass_StartConversation_Condition\n@class:StartConversation\n@method:eventsClass_StartConversation_Condition\n" + message)
	
	def _Condition(self):
		return objects.story.choosenOption == "Začít konverzaci"
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:StartConversation.@method:eventsClass_StartConversation_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:StartConversation"
			method = "@method:eventsClass_StartConversation_Action"
			raise MethodRuntimeException("An Exception in @class:StartConversation during method @method:eventsClass_StartConversation_Action\n@class:StartConversation\n@method:eventsClass_StartConversation_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		
		objects.story.lastMessage.options[0] = "Zeptat se kam má Jakub namířeno" 
		objects.story.lastMessage.options[1] = "Pokračovat v cestě"
		
		objects.story.lastMessage.message  = """
		"Dobrý den," pozdravil Derek. "Kampak se to ubíráte?"
		"Dobrý den," odpověděl muž. "Mířím do Eldhamu. A vy?"
		"Také do Eldhamu," odpověděl Derek. "Cestuji už několik dní z Tarsis."
		"To je pěkná štreka," poznamenal muž. "Já jsem z vesničky nedaleko odtud."
		"To je fajn," usmál se Derek. "Já se jmenuji Derek, obchodník."
		"Já se jmenuji Jakub," představil se muž.
		"Těší mě, Jakube," podal mu Derek ruku.
		
		"Jak se vám líbí cesta?" zeptal se Derek.
		"Je to docela náročné, ale krásné," odpověděl Jakub. "Ta krajina je úchvatná."
		"Souhlasím," přikývl Derek. "A co vezete do Eldhamu?"
		"Vezu pytle s obilím, které prodám na trhu," odpověděl Jakub.
		
		"A co si koupíte za peníze?" zeptal se Derek.
		"Potřebuji nějaké nástroje a zásoby pro farmu," odpověděl Jakub.
		"Já zase nakoupím zásoby na další cestu," řekl Derek.
		"Kam se chystáte dál?" zeptal se Jakub.
		"To ještě nevím," odpověděl Derek. "Uvidím, co mi cesta přinese."
		"To zní dobrodružně," poznamenal Jakub.
		"To ano," usmál se Derek. "Cestování je můj život."
		"Já bych se nikdy neodvážil opustit svůj domov na tak dlouhou dobu," řekl Jakub.
		"Každý má jiné sny," odpověděl Derek.
		"To je pravda," přikývl Jakub.
		"""
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
		"StartConversation",
		"ContinueInTravel",
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
		objects.map.places[1][1] = objects.west_forest_leafy
		objects.map.places[1][2] = objects.deep_forest
		objects.map.places[0][2] = objects.cliff
		
		objects.story.lastMessage = objects.currentStoryMessage
		
		objects.story.lastMessage.clearOptionsArray()
		objects.story.lastMessage.options [0] = "Začít konverzaci"
		objects.story.lastMessage.options [1] = "Pokračovat v cestě"
		
		objects.story.lastMessage.message  = """
		Derek odpovídá obrázku docela běžného obchodníka.
		Jeho cesty obvykle vedly mezi jihovýchodním pobřežím 
		Geparimu a horských oblastí v severnější části až do měst
		v úpatí Korunového pohoří.
		
		V obnošeném kabátci, s brašnou houpací se mu po boku, 
		kráčí po prašné cestě. Slunce se pomalu snáší k obzoru, 
		barví oblohu do odstínů oranžové a fialové. Derek si utahuje 
		šátek kolem krku a zrychluje krok. Cesta do Eldhamu je ještě 
		dlouhá a noc se blíží.
	
		Cestuje už několik dní z Tarsis, rušného obchodního města na 
		jihovýchodním pobřeží Geparimu. Tam prodává kožešiny a sušené ovoce, 
		které si na své cestě z hor nasbíral. V Eldhamu, malebném městečku 
		ležícím v úpatí Korunového pohoří, doufá, že nakoupí zásoby na další 
		cestu.
		
		Po jeho boku klopítá huňatá klisna s hnědou dlouhou srstí.
		Cesta pěkně ubýhá, čas také... Cesta se proplétá mezi hustými lískami.
		
		Po několika hodinách chůze Derek došel na křižovatku. Z druhé cesty se 
		k němu blíží muž oblečený v prostých šatech. Na zádech nese pytle s obilím.	
		"""
		
		objects.time.currentPartOfDay = "afternoon"
		
		# weather can be one of these values:
		#       idle, gentle_rain, rain, heavy_rain, storm
		
		objects.forest_leafy.weather = "idle"
		objects.meadow.weather = "idle"
		objects.mountain_meadow.weather = "idle"
		objects.west_forest_leafy.weather = "idle"
		
		# type of clouds can be one of these:
		# clear, few, scattered, overcast
		objects.forest_leafy.clouds = "clear"
		objects.meadow.clouds = "clear"
		objects.mountain_meadow.clouds = "clear"
		objects.west_forest_leafy.clouds = "clear"
		pass


class event_QuestionAboutWay:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_QuestionAboutWay, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
		"ContinueInTravel",
	]
	
	def loadConditionMethod(self):
		script_path = './QuestionAboutWay_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './QuestionAboutWay_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:QuestionAboutWay.@method:eventsClass_QuestionAboutWay_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:QuestionAboutWay"
			method = "@method:eventsClass_QuestionAboutWay_Condition"
			raise MethodRuntimeException("An Exception in @class:QuestionAboutWay during method @method:eventsClass_QuestionAboutWay_Condition\n@class:QuestionAboutWay\n@method:eventsClass_QuestionAboutWay_Condition\n" + message)
	
	def _Condition(self):
		return objects.story.choosenOption == "Zeptat se kam má Jakub namířeno"
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:QuestionAboutWay.@method:eventsClass_QuestionAboutWay_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:QuestionAboutWay"
			method = "@method:eventsClass_QuestionAboutWay_Action"
			raise MethodRuntimeException("An Exception in @class:QuestionAboutWay during method @method:eventsClass_QuestionAboutWay_Action\n@class:QuestionAboutWay\n@method:eventsClass_QuestionAboutWay_Action\n" + message)
	
	def _Action(self):
		objects.story.lastMessage.clearOptionsArray()
		
		objects.story.lastMessage.options[0] = "Pokračovat v cestě"
		
		objects.story.lastMessage.message  = """
		"Kampak máte namířeno po Eldhamu?" zeptal se Derek.
		"Plánuji se vrátit domů, do své vesnice," odpověděl Jakub. "Tam mě čeká rodina a farma."
		"A co vaši blízcí, cestují také?" zajímal se Derek.
		"Občas, ale většinou zůstávají doma," odpověděl Jakub. "Máme hodně práce kolem farmy."
		"Mohu vás doprovodit na cestě do Eldhamu?" nabídl se Derek.
		"Rád," odpověděl Jakub s úsměvem. "Společnost je vždy vítaná."
		"""
		
		objects.time.currentPartOfDay = "sunset_1"
		pass
		pass


class event_ContinueInTravel:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_ContinueInTravel, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
	]
	
	def loadConditionMethod(self):
		script_path = './ContinueInTravel_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './ContinueInTravel_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		logging.info("run method ---->   @class:ContinueInTravel.@method:eventsClass_ContinueInTravel_Condition")
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:ContinueInTravel"
			method = "@method:eventsClass_ContinueInTravel_Condition"
			raise MethodRuntimeException("An Exception in @class:ContinueInTravel during method @method:eventsClass_ContinueInTravel_Condition\n@class:ContinueInTravel\n@method:eventsClass_ContinueInTravel_Condition\n" + message)
	
	def _Condition(self):
		return objects.story.choosenOption == "Pokračovat v cestě"
		pass
	
	def Action(self):
		logging.info("run method ---->   @class:ContinueInTravel.@method:eventsClass_ContinueInTravel_Action")
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:ContinueInTravel"
			method = "@method:eventsClass_ContinueInTravel_Action"
			raise MethodRuntimeException("An Exception in @class:ContinueInTravel during method @method:eventsClass_ContinueInTravel_Action\n@class:ContinueInTravel\n@method:eventsClass_ContinueInTravel_Action\n" + message)
	
	def _Action(self):
		objects.Derek.location = objects.mountain_meadow
		firstPartOfMessage = ""
		if objects.time.currentPartOfDay == "afternoon":
			objects.time.currentPartOfDay = "sunset_1"
			firstPartOfMessage += """
			... Slunce začíná zapadat.
			Derek rychle pospíchá do nejbližší vesnice, 
			dříve než nastane úplná tma.
			"""
		else:
			objects.time.currentPartOfDay = "night"
			
			firstPartOfMessage += """
			Rychle do nejbližší vesnice, než bude úplná tma.
			Ajaja bajaja
			Tma je tu a Derek nevidí na cestu....
			"Brr to je ale zima,  že já jsem vůbec někam šel..."
			... potemělým krajem zazní dlouhé táhlé "Waůů"...
			"""
		
		objects.story.lastMessage.message = firstPartOfMessage
		pass
		pass


	
class Events:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(Events, cls).__new__(cls)
		return cls._instance
	StartConversation = event_StartConversation()
	Initialize = event_Initialize()
	QuestionAboutWay = event_QuestionAboutWay()
	ContinueInTravel = event_ContinueInTravel()
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