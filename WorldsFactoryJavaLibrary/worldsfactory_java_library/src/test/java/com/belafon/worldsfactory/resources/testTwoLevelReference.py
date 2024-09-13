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

class class_A:
	def __init__(self, id):
		self._id = id
		
	properties = {
		"__object_name__": "@basicType:String",
		"toB": "@class:B"
	}
	@property
	def toB(self):
		return self._toB
		pass
	@toB.setter
	def toB(self, value):
		self._toB = value
		set_property("toB", value, self.properties["toB"], self.__object_name__)
	
	def __str__(self):
		return self._id
	
	def initialize(self):
		logging.info("run method ---->   @class:A.@method:A_initialize")
		try:
			return self._initialize()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:A"
			method = "@method:A_initialize"
			raise MethodRuntimeException("An Exception in @class:A during method @method:A_initialize\n@class:A\n@method:A_initialize\n" + message)
	
	def _initialize(self):
		pass
		pass
	pass


class class_B:
	def __init__(self, id):
		self._id = id
		
	properties = {
		"__object_name__": "@basicType:String",
		"toC": "@class:C"
	}
	@property
	def toC(self):
		return self._toC
		pass
	@toC.setter
	def toC(self, value):
		self._toC = value
		set_property("toC", value, self.properties["toC"], self.__object_name__)
	
	def __str__(self):
		return self._id
	
	def initialize(self):
		logging.info("run method ---->   @class:B.@method:B_initialize")
		try:
			return self._initialize()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:B"
			method = "@method:B_initialize"
			raise MethodRuntimeException("An Exception in @class:B during method @method:B_initialize\n@class:B\n@method:B_initialize\n" + message)
	
	def _initialize(self):
		pass
		pass
	pass


class class_C:
	def __init__(self, id):
		self._id = id
		
	properties = {
		"__object_name__": "@basicType:String",
		"cProperty": "@basicType:String"
	}
	@property
	def cProperty(self):
		return self._cProperty
		pass
	@cProperty.setter
	def cProperty(self, value):
		self._cProperty = value
		set_property("cProperty", value, self.properties["cProperty"], self.__object_name__)
	
	def __str__(self):
		return self._id
	
	def initialize(self):
		logging.info("run method ---->   @class:C.@method:C_initialize")
		try:
			return self._initialize()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:C"
			method = "@method:C_initialize"
			raise MethodRuntimeException("An Exception in @class:C during method @method:C_initialize\n@class:C\n@method:C_initialize\n" + message)
	
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
		cls.a = class_A("@object:a")
		cls.a.__object_name__ = "a"
		cls.b = class_B("@object:b")
		cls.b.__object_name__ = "b"
		cls.c = class_C("@object:c")
		cls.c.__object_name__ = "c"
		try:
			cls.a_initialize(cls.a)
			cls.b_initialize(cls.b)
			cls.c_initialize(cls.c)
		except Exception as e:
			print("_exception_")
			print("Error in python code, while initializing objects:" + str(e))
			print("_exception_end_")
			raise MethodRuntimeException("An Exception in an init method")
		return cls._instance
		pass
	
	def a_initialize(a):
		pass
	
	def b_initialize(b):
		pass
	
	def c_initialize(c):
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
		objects.c.cProperty = "test text"
		objects.b.toC = objects.c
		objects.a.toB = objects.b
		pass


	
class Events:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(Events, cls).__new__(cls)
		return cls._instance
	Initialize = event_Initialize()
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