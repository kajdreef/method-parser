#!/bin/env python3
import os
import argparse
from subprocess import Popen, PIPE
from itertools import repeat

def parse_arguments():
    parser = argparse.ArgumentParser()
    parser.add_argument("tool_dir", help="Path to javaparser-count-methods")
    parser.add_argument("project_path", help="Directory where all projects under study are located.")
    # parser.add_argument("database_location", help="Specify the location of the database.")
    return parser.parse_args()

def build_project(tool_dir):
    build_process = Popen(["./gradlew", "jar"], cwd=tool_dir)
    build_process.wait()

def run_experiment(tool_dir, project):
    # process_experiment = Popen(["java", "-cp", "./build/libs/javaparser-count-methods.jar", "com.kajdreef.method_counter.Launcher", project, database], cwd=tool_dir)
    process_experiment = Popen(["./gradlew", "run", "--args=\'{}\'".format(project)], cwd=tool_dir)
    process_experiment.wait()

if __name__ == "__main__":
    # Parse arguments
    args = parse_arguments()

    # Build project
    build_project(args.tool_dir)

    # Run tool on projects
    directory_iterator = map(lambda f: f.path, filter(lambda f: f.is_dir(), os.scandir(args.project_path)))
    for directory in directory_iterator:
        run_experiment(args.tool_dir, directory)