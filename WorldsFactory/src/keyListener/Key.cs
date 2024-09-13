using System;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace WorldsFactory.keyListener;
public enum KeyAction
{
	up, down
}

/// <summary>
/// This represents a key and type of action.
/// <see cref="KeyNode"/> contains a list of keys that has to activated
/// to perform specific reaction.
/// </summary>
public class Key
{
	[JsonConverter(typeof(StringEnumConverter))]
	[JsonProperty("action")]
	public KeyAction Action { get; set; }

	[JsonConverter(typeof(StringEnumConverter))]
	[JsonProperty("key")]
	public Avalonia.Input.Key Tag { get; set; }
}
