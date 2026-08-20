#!/bin/bash

HEADER_FILE="steve-header.txt"

find . -name "*.java" | while read file; do
    if ! grep -q "Licensed under the Apache License" "$file"; then
        echo "Adding header to $file"
        cat "$HEADER_FILE" "$file" > temp && mv temp "$file"
    else
        echo "Header already exists in $file"
    fi
done

echo "License headers added."
