{
            "type": "record",
            "name": "${name}",
            "aliases": ["LinkedLongs"],                      // old name for this
            "fields" : [
            {"name": "value", "type": "long"},             // each element has a long
            {"name": "next", "type": ["null", "LongList"]} // optional next element
            ]
}