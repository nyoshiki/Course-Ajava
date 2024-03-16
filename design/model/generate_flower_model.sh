#!/bin/sh

python3 ./plantuml2mysql.py DataModel.md flower_db > 4_create_flower_db_tables.sql
python3 ./plantuml2jpaentity.py DataModel.md flower_db 
