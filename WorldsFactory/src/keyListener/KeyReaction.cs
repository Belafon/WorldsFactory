using System;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace WorldsFactory.keyListener;

/// <summary>
/// Key reaction defines the propeties of reaction to 
/// the concrete key combination. the <see cref="KeyNode"/>
/// contains a reaction that should be handled when 
/// the key combination is pressed.
/// The definition (which method should be called) is specified by 
///  <see cref="KeyFunction"/>. That can be added during runtime.
/// </summary>
public class KeyReaction
{
	[JsonProperty("typeFunction")]
	[JsonConverter(typeof(StringEnumConverter))]
	public TypeFunction TypeFunction { get; set; }

	[JsonProperty("function")]
	public string? Name { get; set; }

	[JsonProperty("conditions")]
	public List<string>? Conditions { get; set; }

}
