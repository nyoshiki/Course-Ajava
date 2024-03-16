#!/usr/bin/env python3
#-*-coding:utf-8-*-
# Usage: ./plantuml2jpaentity <dbsource.plu> <dbname>
# Author: Alexander I.Grafov <grafov@gmail.com> and customized by Yusuke Andoh (yusukeandoh0305@gmail.com)
# See https://github.com/grafov/plantuml2mysql
# The code is public domain.

CHARSET="utf8_unicode_ci"

import os
import re
import shutil
import sys
import textwrap
import time

# PlantUML allows some HTML tags in comments.
# We don't want them anymore here...
TAG_RE = re.compile(r'<[^>]+>')
def strip_html_tags(t):
    return TAG_RE.sub('', t)

# A minimal help
def print_usage():
  print("Convert PlantUML classes schema into mySQL database creation script")
  print("Usage:\n", sys.argv[0], "<dbsource.plu> <dbname>")
  print("\nSee https://github.com/grafov/plantuml2mysql for details\n")

# Convert To Camel-case
def to_camel_case(snake_case : str) -> str:
    return re.sub("_(.)",lambda x:x.group(1).upper(), snake_case)

def main():
    
    # Check arguments (exactly 1 + 2):
    if len(sys.argv) != 3:
        print_usage()
        sys.exit()
    
    try: # Avoid exception on STDOUT
        with open(sys.argv[1]) as src:
            data = src.readlines()
    except:
        print("Cannot open file: '" + sys.argv[1] + "'")
        sys.exit()

    if os.path.exists("domain"):
        shutil.rmtree("domain")

    os.makedirs("domain")
    
    uml = False; table = False; field = False
    pk = False; idx = False
    clsname = ""
    clslines = []

    for l in data:
        l = l.strip()
        if not l:
            continue
        if l == "@startuml":
            uml = True
            continue
        if not uml:
            continue
        if l == "--": # Separator
            continue

        comment = ""
        notnull = False
        i = l.split()
        fname = i[0]

        if fname == ".." or fname == "__": # Separators in table definition
            continue
        # Comment
        if field and ("--" in l):
            i, comment = l.split("--", 2)
            i = i.split()
        # Not Null
        if field and ("not null" in l):
            notnull = True

        pk = False; idx = False

        # Primary Key or Index
        if fname[0] in ("+", "#"):
            if fname[0] == "#":
                pk = True
            else:
                idx = True
            fname = fname[1:]
        if l == "@enduml":
            uml = False
            continue
        if not uml:
             continue

        # Start Entity Definition
        if l.startswith("class"):

            table = True; field = False
            clsname = to_camel_case(i[1])
            clsname = clsname[0].upper() + clsname[1:]

            print(f"Generating {clsname}.java ...")
            clslines = []
            clslines.append(textwrap.dedent('''\
                package jp.flowershop.domain;

                import java.io.Serializable;
                import javax.persistence.Column;
                import javax.persistence.Entity;
                import javax.persistence.Id;
                import lombok.Getter;
                import lombok.Setter;
                import lombok.ToString;

                @Entity
                @ToString
            '''))
            clslines.append(f"public class {clsname} implements Serializable {{\n\n")
            clslines.append("    private static final long serialVersionUID = 1L;\n");
            continue
        if table and not field and l == "==": # Seperator after table description
            field = True
            continue
        # End Entity Definition
        if field and l == "}":
            print(f"End Generate {clsname}")
            table = False; field = False
            clslines.append("}\n")

            with open(f"domain/{clsname}.java", "w", encoding = "utf_8") as javafile:
                javafile.write(''.join(clslines))
            print(f"End Output File {clsname}.java")
            continue
        # Field Definition
        if field:
            print(f"  Generate {fname}")
            clslines.append("    @Getter\n")
            clslines.append("    @Setter\n")
            if notnull and pk:
                clslines.append("    @Column(unique = true, nullable = false)\n")
                clslines.append("    @Id\n")
            if notnull and not pk:
                clslines.append("    @Column(nullable = false)\n")
            if comment:
                clslines.append(f"    //{strip_html_tags(comment.strip())}\n")
            
            fieldtype = "String"
            if i[2] == "int":
                fieldtype = "Integer"
            fname = to_camel_case(fname)
            clslines.append(f"    private {fieldtype} {fname};\n\n")

if __name__ == "__main__":
    main()