--
-- Created by IntelliJ IDEA.
-- User: hei
-- Date: 26/02/21
-- Time: 20:57
-- To change this template use File | Settings | File Templates.
--

OR, XOR, AND = 1, 3, 4

function bitoper(a, b, oper)
    local r, m, s = 0, 2^31, nil
    repeat
        s, a, b = a + b + m, a % m, b % m
        r, m = r + m * oper % (s - a - b), m / 2
    until m < 1
    return r
end

function lshift(x, by)
    return x * 2 ^ by
end

function rshift(x, by)
    return math.floor(x / 2 ^ by)
end

function shortSignedAtOffset(bytes, offset)
    local lowerByte = bitoper(bytes[offset], 0xFF, AND)
    local upperByte = bytes[offset + 1]
    return lshift(upperByte, 8) + lowerByte
end

function shortUnsignedAtOffset(bytes, offset)
    local lowerByte = bitoper(bytes[offset], 0xFF, AND)
    local upperByte = bitoper(bytes[offset + 1], 0xFF, AND)
    return lshift(upperByte, 8) + lowerByte
end

function twentyFourBitUnsignedAtOffset(bytes, offset)
    local lowerByte = bitoper(bytes[offset], 0xFF, AND)
    local mediumByte = bitoper(bytes[offset + 1], 0xFF, AND)
    local upperByte = bitoper(bytes[offset + 2], 0xFF, AND)
    return lshift(upperByte, 16) + lshift(mediumByte, 8) + lowerByte
end
