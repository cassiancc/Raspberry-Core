/*
MIT License

Copyright (c) 2020 vectorwing

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package vectorwing.farmersdelight.integration.emi;

import dev.emi.emi.api.stack.EmiStack;
import vectorwing.farmersdelight.common.registry.ModItems;

public class FDRecipeWorkstations {
    public static final EmiStack COOKING_POT = EmiStack.of(ModItems.COOKING_POT.get());
    public static final EmiStack CUTTING_BOARD = EmiStack.of(ModItems.CUTTING_BOARD.get());
    public static final EmiStack ORGANIC_COMPOST = EmiStack.of(ModItems.ORGANIC_COMPOST.get());
}
