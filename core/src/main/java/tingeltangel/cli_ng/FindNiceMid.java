/*
 * Copyright 2016 martin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tingeltangel.cli_ng;

import java.util.Arrays;
import java.util.HashSet;
import tingeltangel.core.Book;
import tingeltangel.core.Repository;
import tingeltangel.core.Translator;

/**
 *
 * @author martin
 */
class FindNiceMid extends CliCmd {

    @Override
    public String getName() {
        return("find-nice-mid");
    }

    @Override
    public String getDescription() {
        return("find-nice-mid");
    }

    private final static int[][] NICE_MIDS = {
        // real nice mids (free and far away from official mids)
        {8000, 8500},
        {9500, Translator.MAX_MID},
        // nice mids  (non official mids)
        {6000, 8799},
        {8900, Translator.MAX_MID},
        // all ids
        {1, Translator.MAX_MID}
    };
    
    @Override
    public int execute(String[] args) {
        
        HashSet<Integer> mids = new HashSet<Integer>(Arrays.asList(Repository.getIDs()));
        mids.addAll(Book.getBookMIDs());
        
        // mids with known code ids
        for(int i = 0; i < NICE_MIDS.length; i++) {
            for(int mid = NICE_MIDS[i][0]; mid <= NICE_MIDS[i][0]; mid++) {
                if(!mids.contains(mid)) {
                    if(Translator.isKnownTingID(mid)) {
                        System.out.println(Integer.toString(mid));
                        return(ok());
                    }
                }
            }
        }
        
        // mids with unknown code ids (thats quite bad!)
        for(int i = 0; i < NICE_MIDS.length; i++) {
            for(int mid = NICE_MIDS[i][0]; mid <= NICE_MIDS[i][0]; mid++) {
                if(!mids.contains(mid)) {
                    if(!Translator.isKnownTingID(mid)) {
                        System.out.println(Integer.toString(mid));
                        return(ok());
                    }
                }
            }
        }
        
        return(error("Keine freie MID gefunden"));
    }
}
