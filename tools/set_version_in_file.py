import argparse

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Process some integers.')
    parser.add_argument('--version', dest="version",  type=str,
                        help='Next versions name')
    parser.add_argument('--pom', dest="pom_location",  type=str,
                        help='POM to be updated')

    args = parser.parse_args()

    from xml.etree import ElementTree as et
    et.register_namespace('', 'http://maven.apache.org/POM/4.0.0')
    tree = et.parse(args.pom_location)
    root = tree.getroot()
    version = root.find('{http://maven.apache.org/POM/4.0.0}version')
    version.text = args.version
    tree.write(args.pom_location)
