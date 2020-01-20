def left(field, length):
    diff = length - len(str(field))
    return str(field) + " " * diff


def center(field, length):
    if isinstance(field, list):
        diff = length - len(str(field[0]))
        return " " * (diff / 2) + str(field[0]) + " " * (length - len(str(field[0])) - (diff / 2))
    else:
        diff = length - len(str(field))
        return " " * (diff / 2) + str(field) + " " * (length - len(str(field)) - (diff / 2))


def right(field, length):
    diff = length - len(str(field))
    return " " * diff + str(field)


def pprinttable(rows, fields):
    output = buildtable(rows, fields)
    for line in output:
        print line
    return output

def buildtable(rows, fields):
    str_list = []

    if len(rows) > 0:
        # headers = HEADER._fields
        # headers = HEADER
        lens = []
        for field in fields:
            lens.append(len(field[1]))

        for row in rows:
            inc = 0
            for field in fields:
                if isinstance(row[field[0]], (int, float, long)):
                    if lens[inc] < 4:
                        lens[inc] = 4
                    if lens[inc] < len(str(row[field[0]])):
                        lens[inc] = len(str(row[field[0]]))
                    # if lens[inc] < 16:
                    #     lens[inc] = 16
                elif isinstance(row[field[0]], (list, tuple)):
                    size = 2
                    for i in range(len(row[field[0]])):
                        size += len(row[field[0]][i]) + 3
                    if size > lens[inc]:
                        lens[inc] = size
                elif isinstance(row[field[0]], (dict)):
                    size = 2
                    for i in range(len(row[field[0]])):
                        size += len(row[field[0]]) + 3
                    if size > lens[inc]:
                        lens[inc] = size
                else:
                    if row[field[0]] is not None and (len(row[field[0]]) > lens[inc]):
                        lens[inc] = len(row[field[0]])
                inc += 1

        headerRowSeparator = ""
        headerRow = ""
        for loc in range(len(fields)):
            headerRowSeparator = headerRowSeparator + "|" + "=" * (lens[loc]+1)
            headerRow = headerRow + "| " + center([fields[loc][1]], lens[loc])

        headerRowSeparator = headerRowSeparator + "|"
        headerRow = headerRow + "|"

        str_list.append(headerRowSeparator)
        # print headerRowSeparator
        str_list.append(headerRow)
        # print headerRow
        str_list.append(headerRowSeparator)
        # print headerRowSeparator

        for row in rows:
            inc = 0
            recordRow = ""
            offset = 0
            for field in fields:
                if isinstance(row[field[0]], int) or isinstance(row[field[0]], float) or isinstance(row[field[0]], long):
                    recordRow = recordRow + "| " + right(row[field[0]], lens[inc])
                # elif isinstance(row[field[0]], bool):
                #     if row[field[0]]:
                #         recordRow = recordRow + "| " + right('X', lens[inc])
                #     else:
                #         recordRow = recordRow + "| " + right('', lens[inc])

                elif isinstance(row[field[0]], (dict)):
                    # recordRow = recordRow + "| "
                    offset = len(recordRow)
                    it = 0
                    for item in row[field[0]]:
                        dictItem = str(row[field[0]][item])
                        if it == 0:
                            recordRow = recordRow + '|' + left(dictItem, lens[inc] + 1) + '|\n|'
                        elif it == len(row[field[0]]) - 1:
                            recordRow = recordRow + ' '.rjust(offset-1) + '|' + left(dictItem, lens[inc] + 1)
                        else:
                            recordRow = recordRow + ' '.rjust(offset-1) + '|' + left(dictItem, lens[inc] + 1) + '|\n|'
                        it += 1
                else:
                    recordRow = recordRow + "| " + left(row[field[0]], lens[inc])
                inc += 1
            recordRow = recordRow + "|"

            str_list.append(recordRow)
            # print recordRow

        str_list.append(headerRowSeparator)
        # print headerRowSeparator
    return str_list


def pprinttable2(rows, fields):
    output = buildtable2(rows, fields)
    for line in output:
        print line


def buildtable2(rows, fields):
    str_list = []

    if len(rows) > 0:
        # headers = HEADER._fields
        # headers = HEADER
        lens = []
        for field in fields:
            lens.append(len(field))

        for row in rows:
            inc = 0
            for field in fields:
                try:
                    value = row[field]
                    if isinstance(row[field], (int, float, long)):
                        if lens[inc] < 4:
                            lens[inc] = 4
                        if lens[inc] < len(str(row[field])):
                            lens[inc] = len(str(row[field]))
                        # if lens[inc] < 16:
                        #     lens[inc] = 16
                    elif isinstance(row[field], (list, tuple)):
                        size = 2
                        for i in range(len(row[field])):
                            size += len(row[field][i]) + 3
                        if size > lens[inc]:
                            lens[inc] = size
                    elif isinstance(row[field], (dict)):
                        size = 2
                        for i in range(len(row[field])):
                            size += len(row[field]) + 3
                        if size > lens[inc]:
                            lens[inc] = size
                    else:
                        if row[field] is not None and (len(row[field]) > lens[inc]):
                            lens[inc] = len(row[field])
                except:
                    pass
                inc += 1

        headerRowSeparator = ""
        headerRow = ""
        loc = 0
        for field in fields:
        # for loc in range(len(fields)):
            headerRowSeparator = headerRowSeparator + "|" + "=" * (lens[loc]+1)
            headerRow = headerRow + "| " + center(field, lens[loc])
            loc += 1

        headerRowSeparator = headerRowSeparator + "|"
        headerRow = headerRow + "|"

        str_list.append(headerRowSeparator)
        # print headerRowSeparator
        str_list.append(headerRow)
        # print headerRow
        str_list.append(headerRowSeparator)
        # print headerRowSeparator

        for row in rows:
            inc = 0
            recordRow = ""
            offset = 0
            for field in fields:
                try:
                    value = row[field]
                    if isinstance(row[field], int) or isinstance(row[field], float) or isinstance(row[field], long):
                        recordRow = recordRow + "| " + right(row[field], lens[inc])
                    # elif isinstance(row[field[0]], bool):
                    #     if row[field[0]]:
                    #         recordRow = recordRow + "| " + right('X', lens[inc])
                    #     else:
                    #         recordRow = recordRow + "| " + right('', lens[inc])

                    elif isinstance(row[field], (dict)):
                        # recordRow = recordRow + "| "
                        offset = len(recordRow)
                        it = 0
                        for item in row[field]:
                            dictItem = str(item) + ':' + str(row[field][item])
                            if it == 0:
                                recordRow = recordRow + '|' + left(dictItem, lens[inc] + 1) + '|\n|'
                            elif it == len(row[field]) - 1:
                                recordRow = recordRow + ' '.rjust(offset-1) + '|' + left(dictItem, lens[inc] + 1)
                            else:
                                recordRow = recordRow + ' '.rjust(offset-1) + '|' + left(dictItem, lens[inc] + 1) + '|\n|'
                            it += 1
                    else:
                        recordRow = recordRow + "| " + left(row[field], lens[inc])
                except:
                    recordRow = recordRow + "| " + left(' ', lens[inc])

                inc += 1

            recordRow = recordRow + "|"

            str_list.append(recordRow)
            # print recordRow

        str_list.append(headerRowSeparator)
        # print headerRowSeparator
    return str_list
