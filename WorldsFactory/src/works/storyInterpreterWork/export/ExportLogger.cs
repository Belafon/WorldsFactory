using System.Text;
using Avalonia.Platform.Storage;
using WorldsFactory.world;
using IronPython.Hosting;
using System.Text.RegularExpressions;
using Serilog;
using System.Data;
using WorldsFactory.world.events;
using WorldsFactory.works.storyInterpreterWork.export;

namespace WorldsFactory.works.storyInterpreterWork.export;
public class ExportLogger 
{
	
	internal void Export(out StringBuilder code)
	{
		code = new StringBuilder();
		code.Append(
"""
import logging

logging.basicConfig(filename='story.log', encoding='utf-8', level=logging.INFO, format='%(asctime)s %(message)s', datefmt='%m/%d/%Y %I:%M:%S %p')
logging.info('Loading python code')
""");
		code.Append("\n");
	}
}